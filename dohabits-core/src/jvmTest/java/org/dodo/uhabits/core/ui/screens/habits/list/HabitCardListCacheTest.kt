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
package com.dodo.dohabits.core.ui.screens.habits.list

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import com.dodo.dohabits.core.BaseUnitTest
import com.dodo.dohabits.core.commands.CreateRepetitionCommand
import com.dodo.dohabits.core.commands.DeleteHabitsCommand
import com.dodo.dohabits.core.models.Entry
import com.dodo.dohabits.core.utils.DateUtils.Companion.getToday
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

class HabitCardListCacheTest : BaseUnitTest() {
    private lateinit var cache: HabitCardListCache
    private lateinit var listener: HabitCardListCache.Listener
    var today = getToday()

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        habitList.removeAll()
        for (i in 0..9) {
            if (i == 3) habitList.add(fixtures.createLongHabit()) else habitList.add(fixtures.createShortHabit())
        }
        cache = HabitCardListCache(habitList, commandRunner, taskRunner, mock())
        cache.setCheckmarkCount(10)
        cache.refreshAllHabits()
        cache.onAttached()
        listener = mock()
        cache.setListener(listener)
    }

    override fun tearDown() {
        cache.onDetached()
    }

    @Test
    fun testCommandListener_all() {
        assertThat(cache.habitCount, equalTo(10))
        val h = habitList.getByPosition(0)
        commandRunner.run(
            DeleteHabitsCommand(habitList, listOf(h))
        )
        verify(listener).onItemRemoved(0)
        verify(listener).onRefreshFinished()
        assertThat(cache.habitCount, equalTo(9))
    }

    @Test
    fun testCommandListener_single() {
        val h2 = habitList.getByPosition(2)
        commandRunner.run(CreateRepetitionCommand(habitList, h2, today, Entry.NO, ""))
        verify(listener).onItemChanged(2)
        verify(listener).onRefreshFinished()
        verifyNoMoreInteractions(listener)
    }

    @Test
    fun testGet() {
        assertThat(cache.habitCount, equalTo(10))
        val h = habitList.getByPosition(3)
        val score = h.scores[today].value
        assertThat(cache.getHabitByPosition(3), equalTo(h))
        assertThat(cache.getScore(h.id!!), equalTo(score))
        val actualCheckmarks = cache.getCheckmarks(h.id!!)

        val expectedCheckmarks = h
            .computedEntries
            .getByInterval(today.minus(9), today)
            .map { it.value }.toIntArray()
        assertThat(actualCheckmarks, equalTo(expectedCheckmarks))
    }

    @Test
    fun testRemoval() {
        removeHabitAt(0)
        removeHabitAt(3)
        cache.refreshAllHabits()
        verify(listener).onItemRemoved(0)
        verify(listener).onItemRemoved(3)
        verify(listener).onRefreshFinished()
        assertThat(cache.habitCount, equalTo(8))
    }

    @Test
    fun testRefreshWithNoChanges() {
        cache.refreshAllHabits()
        verify(listener).onRefreshFinished()
        verifyNoMoreInteractions(listener)
    }

    @Test
    fun testReorder_onCache() {
        val h2 = cache.getHabitByPosition(2)
        val h3 = cache.getHabitByPosition(3)
        val h7 = cache.getHabitByPosition(7)
        cache.reorder(2, 7)
        assertThat(cache.getHabitByPosition(2), equalTo(h3))
        assertThat(cache.getHabitByPosition(7), equalTo(h2))
        assertThat(cache.getHabitByPosition(6), equalTo(h7))
        verify(listener).onItemMoved(2, 7)
        verifyNoMoreInteractions(listener)
    }

    @Test
    fun testReorder_onList() {
        val h2 = habitList.getByPosition(2)
        val h3 = habitList.getByPosition(3)
        val h7 = habitList.getByPosition(7)
        assertThat(cache.getHabitByPosition(2), equalTo(h2))
        assertThat(cache.getHabitByPosition(7), equalTo(h7))
        reset(listener)
        habitList.reorder(h2, h7)
        cache.refreshAllHabits()
        assertThat(cache.getHabitByPosition(2), equalTo(h3))
        assertThat(cache.getHabitByPosition(7), equalTo(h2))
        assertThat(cache.getHabitByPosition(6), equalTo(h7))
        verify(listener).onItemMoved(3, 2)
        verify(listener).onItemMoved(4, 3)
        verify(listener).onItemMoved(5, 4)
        verify(listener).onItemMoved(6, 5)
        verify(listener).onItemMoved(7, 6)
        verify(listener).onRefreshFinished()
        verifyNoMoreInteractions(listener)
    }

    private fun removeHabitAt(position: Int) {
        val h = habitList.getByPosition(position)
        habitList.remove(h)
    }
}
