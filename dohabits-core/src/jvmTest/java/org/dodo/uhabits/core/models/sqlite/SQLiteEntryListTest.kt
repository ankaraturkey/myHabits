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

import com.dodo.dohabits.core.BaseUnitTest.Companion.buildMemoryDatabase
import com.dodo.dohabits.core.database.Repository
import com.dodo.dohabits.core.models.Entry
import com.dodo.dohabits.core.models.Entry.Companion.UNKNOWN
import com.dodo.dohabits.core.models.Timestamp
import com.dodo.dohabits.core.models.sqlite.records.EntryRecord
import com.dodo.dohabits.core.utils.DateUtils
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SQLiteEntryListTest {

    private val database = buildMemoryDatabase()
    private val repository = Repository(EntryRecord::class.java, database)
    private val entries = SQLiteEntryList(database)
    private val today = DateUtils.getToday()

    @Before
    fun setUp() {
        // Create a habit and add it to the database to satisfy foreign key requirements
        val factory = SQLModelFactory(database)
        val habitList = factory.buildHabitList()
        val habit = factory.buildHabit()
        habitList.add(habit)
        entries.habitId = habit.id
    }

    @Test
    fun testLoad() {
        val today = DateUtils.getToday()
        repository.save(
            EntryRecord().apply {
                habitId = entries.habitId
                timestamp = today.unixTime
                value = 500
            }
        )
        repository.save(
            EntryRecord().apply {
                habitId = entries.habitId
                timestamp = today.minus(5).unixTime
                value = 300
            }
        )
        assertEquals(
            Entry(timestamp = today, value = 500),
            entries.get(today)
        )
        assertEquals(
            Entry(timestamp = today.minus(1), value = UNKNOWN),
            entries.get(today.minus(1))
        )
        assertEquals(
            Entry(timestamp = today.minus(5), value = 300),
            entries.get(today.minus(5))
        )
    }

    @Test
    fun testAdd() {
        assertNull(getByTimestamp(1, today))

        val original = Entry(today, 150)
        entries.add(original)

        val retrieved = getByTimestamp(1, today)!!
        assertEquals(original, retrieved.toEntry())

        val replacement = Entry(today, 90)
        entries.add(replacement)

        val retrieved2 = getByTimestamp(1, today)!!
        assertEquals(replacement, retrieved2.toEntry())
    }

    private fun getByTimestamp(habitId: Int, timestamp: Timestamp): EntryRecord? {
        return repository.findFirst(
            "where habit = ? and timestamp = ?",
            habitId.toString(),
            timestamp.unixTime.toString()
        )
    }
}
