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

import com.dodo.dohabits.core.BaseUnitTest
import com.dodo.dohabits.core.models.Habit
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UnarchiveHabitsCommandTest : BaseUnitTest() {
    private lateinit var command: UnarchiveHabitsCommand
    private lateinit var habit: Habit

    @Before
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        habit = fixtures.createShortHabit()
        habit.isArchived = true
        habitList.add(habit)
        command = UnarchiveHabitsCommand(habitList, listOf(habit))
    }

    @Test
    fun testExecuteUndoRedo() {
        assertTrue(habit.isArchived)
        command.run()
        assertFalse(habit.isArchived)
    }
}
