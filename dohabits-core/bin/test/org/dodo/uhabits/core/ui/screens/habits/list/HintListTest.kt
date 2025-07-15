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

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import com.dodo.dohabits.core.BaseUnitTest
import com.dodo.dohabits.core.models.Timestamp
import com.dodo.dohabits.core.preferences.Preferences
import com.dodo.dohabits.core.utils.DateUtils.Companion.getToday
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HintListTest : BaseUnitTest() {
    private lateinit var hintList: HintList
    private lateinit var hints: Array<String>

    private val prefs: Preferences = mock()
    private lateinit var today: Timestamp
    private lateinit var yesterday: Timestamp

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        today = getToday()
        yesterday = today.minus(1)
        hints = arrayOf("hint1", "hint2", "hint3")
        hintList = HintList(prefs, hints)
    }

    @Test
    @Throws(Exception::class)
    fun pop() {
        whenever(prefs.lastHintNumber).thenReturn(-1)
        assertThat(hintList.pop(), equalTo("hint1"))
        verify(prefs).updateLastHint(0, today)
        whenever(prefs.lastHintNumber).thenReturn(2)
        assertNull(hintList.pop())
    }

    @Test
    @Throws(Exception::class)
    fun shouldShow() {
        whenever(prefs.lastHintTimestamp).thenReturn(today)
        assertFalse(hintList.shouldShow())
        whenever(prefs.lastHintTimestamp).thenReturn(yesterday)
        assertTrue(hintList.shouldShow())
    }
}
