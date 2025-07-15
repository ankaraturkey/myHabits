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
package com.dodo.dohabits.core.models.sqlite

import com.dodo.dohabits.core.database.Database
import com.dodo.dohabits.core.database.Repository
import com.dodo.dohabits.core.models.EntryList
import com.dodo.dohabits.core.models.ModelFactory
import com.dodo.dohabits.core.models.ScoreList
import com.dodo.dohabits.core.models.StreakList
import com.dodo.dohabits.core.models.sqlite.records.EntryRecord
import com.dodo.dohabits.core.models.sqlite.records.HabitRecord
import javax.inject.Inject

/**
 * Factory that provides models backed by an SQLite database.
 */
class SQLModelFactory
@Inject constructor(
    val database: Database
) : ModelFactory {
    override fun buildOriginalEntries() = SQLiteEntryList(database)
    override fun buildComputedEntries() = EntryList()
    override fun buildHabitList() = SQLiteHabitList(this)
    override fun buildScoreList() = ScoreList()
    override fun buildStreakList() = StreakList()

    override fun buildHabitListRepository() =
        Repository(HabitRecord::class.java, database)

    override fun buildRepetitionListRepository() =
        Repository(EntryRecord::class.java, database)
}
