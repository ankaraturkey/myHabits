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
package com.dodo.dohabits.core.tasks

import com.dodo.dohabits.core.io.HabitsCSVExporter
import com.dodo.dohabits.core.models.Habit
import com.dodo.dohabits.core.models.HabitList
import java.io.File

class ExportCSVTask(
    private val habitList: HabitList,
    private val selectedHabits: List<Habit>,
    private val outputDir: File,
    private val listener: Listener
) : Task {
    private var archiveFilename: String? = null
    override fun doInBackground() {
        try {
            val exporter = HabitsCSVExporter(habitList, selectedHabits, outputDir)
            archiveFilename = exporter.writeArchive()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPostExecute() {
        listener.onExportCSVFinished(archiveFilename)
    }

    fun interface Listener {
        fun onExportCSVFinished(archiveFilename: String?)
    }
}
