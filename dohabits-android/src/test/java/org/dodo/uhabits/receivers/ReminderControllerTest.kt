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
package com.dodo.dohabits.receivers

import com.dodo.dohabits.BaseAndroidJVMTest
import com.dodo.dohabits.core.models.Habit
import com.dodo.dohabits.core.models.Timestamp
import com.dodo.dohabits.core.preferences.Preferences
import com.dodo.dohabits.core.reminders.ReminderScheduler
import com.dodo.dohabits.core.ui.NotificationTray
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

class ReminderControllerTest : BaseAndroidJVMTest() {
    private lateinit var controller: ReminderController
    private lateinit var reminderScheduler: ReminderScheduler
    private lateinit var notificationTray: NotificationTray
    private lateinit var preferences: Preferences
    override fun setUp() {
        super.setUp()
        reminderScheduler = mock()
        notificationTray = mock()
        preferences = mock()
        controller = ReminderController(
            reminderScheduler,
            notificationTray,
            preferences
        )
    }

    @Test
    @Throws(Exception::class)
    fun testOnDismiss() {
        verifyNoMoreInteractions(reminderScheduler)
        verifyNoMoreInteractions(notificationTray)
        verifyNoMoreInteractions(preferences)
    }

    @Test
    @Throws(Exception::class)
    fun testOnShowReminder() {
        val habit: Habit = mock()
        controller.onShowReminder(habit, Timestamp.ZERO.plus(100), 456)
        verify(notificationTray).show(habit, Timestamp.ZERO.plus(100), 456)
        verify(reminderScheduler).scheduleAll()
    }

    @Test
    @Throws(Exception::class)
    fun testOnBootCompleted() {
        controller.onBootCompleted()
        verify(reminderScheduler).scheduleAll()
    }
}
