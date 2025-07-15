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

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.dodo.dohabits.core.AppScope
import com.dodo.dohabits.core.models.Habit
import com.dodo.dohabits.core.models.Timestamp
import com.dodo.dohabits.core.preferences.Preferences
import com.dodo.dohabits.core.reminders.ReminderScheduler
import com.dodo.dohabits.core.ui.NotificationTray
import com.dodo.dohabits.core.utils.DateUtils.Companion.getUpcomingTimeInMillis
import com.dodo.dohabits.notifications.SnoozeDelayPickerActivity
import javax.inject.Inject

@AppScope
class ReminderController @Inject constructor(
    private val reminderScheduler: ReminderScheduler,
    private val notificationTray: NotificationTray,
    private val preferences: Preferences
) {
    fun onBootCompleted() {
        reminderScheduler.scheduleAll()
    }

    fun onShowReminder(
        habit: Habit,
        timestamp: Timestamp,
        reminderTime: Long
    ) {
        notificationTray.show(habit, timestamp, reminderTime)
        reminderScheduler.scheduleAll()
    }

    fun onSnoozePressed(habit: Habit, context: Context) {
        showSnoozeDelayPicker(habit, context)
    }

    fun onSnoozeDelayPicked(habit: Habit, delayInMinutes: Int) {
        reminderScheduler.snoozeReminder(habit, delayInMinutes.toLong())
        notificationTray.cancel(habit)
    }

    fun onSnoozeTimePicked(habit: Habit?, hour: Int, minute: Int) {
        val time: Long = getUpcomingTimeInMillis(hour, minute)
        reminderScheduler.scheduleAtTime(habit!!, time)
        notificationTray.cancel(habit)
    }

    fun onDismiss(habit: Habit) {
        if (preferences.shouldMakeNotificationsSticky()) {
            // This is a workaround to keep sticky notifications non-dismissible in Android 14+.
            // If the notification is dismissed, we immediately reshow it.
            notificationTray.reshow(habit)
        } else {
            notificationTray.cancel(habit)
        }
    }

    private fun showSnoozeDelayPicker(habit: Habit, context: Context) {
        context.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
        val intent = Intent(context, SnoozeDelayPickerActivity::class.java)
        intent.data = Uri.parse(habit.uriString)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
