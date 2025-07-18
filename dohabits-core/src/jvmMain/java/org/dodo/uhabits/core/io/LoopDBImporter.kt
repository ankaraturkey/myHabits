/*
 * Copyright (C) 2016-2021 Álinson Santos Xavier <git@axavier.org>
 *
 * This file is part of Daily Loop Tracker.
 *
 * Daily Loop Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Daily Loop Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.dodo.dohabits.core.io

import com.dodo.dohabits.core.AppScope
import com.dodo.dohabits.core.DATABASE_VERSION
import com.dodo.dohabits.core.commands.CommandRunner
import com.dodo.dohabits.core.commands.CreateHabitCommand
import com.dodo.dohabits.core.commands.EditHabitCommand
import com.dodo.dohabits.core.database.DatabaseOpener
import com.dodo.dohabits.core.database.MigrationHelper
import com.dodo.dohabits.core.database.Repository
import com.dodo.dohabits.core.models.Entry
import com.dodo.dohabits.core.models.HabitList
import com.dodo.dohabits.core.models.ModelFactory
import com.dodo.dohabits.core.models.Timestamp
import com.dodo.dohabits.core.models.sqlite.records.EntryRecord
import com.dodo.dohabits.core.models.sqlite.records.HabitRecord
import com.dodo.dohabits.core.utils.isSQLite3File
import java.io.File
import javax.inject.Inject

/**
 * Class that imports data from database files exported by Daily Loop Tracker.
 */
class LoopDBImporter
@Inject constructor(
    @AppScope val habitList: HabitList,
    @AppScope val modelFactory: ModelFactory,
    @AppScope val opener: DatabaseOpener,
    @AppScope val runner: CommandRunner,
    @AppScope logging: Logging
) : AbstractImporter() {

    private val logger = logging.getLogger("LoopDBImporter")

    override fun canHandle(file: File): Boolean {
        if (!file.isSQLite3File()) return false
        val db = opener.open(file)
        var canHandle = true
        val c = db.query("select count(*) from SQLITE_MASTER where name='Habits' or name='Repetitions'")
        if (!c.moveToNext() || c.getInt(0) != 2) {
            logger.error("Cannot handle file: tables not found")
            canHandle = false
        }
        if (db.version > DATABASE_VERSION) {
            logger.error("Cannot handle file: incompatible version: ${db.version} > $DATABASE_VERSION")
            canHandle = false
        }
        c.close()
        db.close()
        return canHandle
    }

    override fun importHabitsFromFile(file: File) {
        val db = opener.open(file)
        val helper = MigrationHelper(db)
        helper.migrateTo(DATABASE_VERSION)

        val habitsRepository = Repository(HabitRecord::class.java, db)
        val entryRepository = Repository(EntryRecord::class.java, db)

        for (habitRecord in habitsRepository.findAll("order by position")) {
            var habit = habitList.getByUUID(habitRecord.uuid)
            val entryRecords = entryRepository.findAll("where habit = ?", habitRecord.id.toString())

            if (habit == null) {
                habit = modelFactory.buildHabit()
                habitRecord.id = null
                habitRecord.copyTo(habit)
                CreateHabitCommand(modelFactory, habitList, habit).run()
            } else {
                val modified = modelFactory.buildHabit()
                habitRecord.id = habit.id
                habitRecord.copyTo(modified)
                EditHabitCommand(habitList, habit.id!!, modified).run()
            }

            // Reload saved version of the habit
            habit = habitList.getByUUID(habitRecord.uuid)!!
            val entries = habit.originalEntries

            // Import entries
            for (r in entryRecords) {
                val t = Timestamp(r.timestamp!!)
                val (_, value, notes) = entries.get(t)
                if (value != r.value || notes != r.notes) {
                    entries.add(Entry(t, r.value!!, r.notes ?: ""))
                }
            }
            habit.recompute()
        }
        habitList.resort()
        db.close()
    }
}
