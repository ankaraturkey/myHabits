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
package com.dodo.dohabits.core.models

import com.dodo.dohabits.core.database.Repository
import com.dodo.dohabits.core.models.sqlite.records.EntryRecord
import com.dodo.dohabits.core.models.sqlite.records.HabitRecord

/**
 * Interface implemented by factories that provide concrete implementations of
 * the core model classes.
 */
interface ModelFactory {

    fun buildHabit(): Habit {
        val scores = buildScoreList()
        val streaks = buildStreakList()
        return Habit(
            scores = scores,
            streaks = streaks,
            originalEntries = buildOriginalEntries(),
            computedEntries = buildComputedEntries()
        )
    }
    fun buildComputedEntries(): EntryList
    fun buildOriginalEntries(): EntryList
    fun buildHabitList(): HabitList
    fun buildScoreList(): ScoreList
    fun buildStreakList(): StreakList
    fun buildHabitListRepository(): Repository<HabitRecord>
    fun buildRepetitionListRepository(): Repository<EntryRecord>
}
