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
package com.dodo.dohabits.core.commands

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import com.dodo.dohabits.core.BaseUnitTest
import com.dodo.dohabits.core.models.Frequency
import com.dodo.dohabits.core.models.Habit
import com.dodo.dohabits.core.models.Timestamp
import com.dodo.dohabits.core.utils.DateUtils.Companion.getTodayWithOffset
import org.junit.Before
import org.junit.Test

class EditHabitCommandTest : BaseUnitTest() {
    private lateinit var command: EditHabitCommand
    private lateinit var habit: Habit
    private lateinit var modified: Habit
    private lateinit var today: Timestamp

    @Before
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        habit = fixtures.createShortHabit()
        habit.name = "original"
        habit.frequency = Frequency.DAILY
        habit.recompute()
        habitList.add(habit)
        modified = fixtures.createEmptyHabit()
        modified.copyFrom(habit)
        modified.name = "modified"
        habitList.add(modified)
        today = getTodayWithOffset()
    }

    @Test
    fun testExecute() {
        command = EditHabitCommand(habitList, habit.id!!, modified)
        val originalScore = habit.scores[today].value
        assertThat(habit.name, equalTo("original"))
        command.run()
        assertThat(habit.name, equalTo("modified"))
        assertThat(habit.scores[today].value, equalTo(originalScore))
    }
}
