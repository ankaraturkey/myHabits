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

package com.dodo.dohabits.intents

import android.app.AlarmManager
import android.app.AlarmManager.RTC
import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.os.Build
import android.util.Log
import com.dodo.dohabits.core.AppScope
import com.dodo.dohabits.core.models.Habit
import com.dodo.dohabits.core.reminders.ReminderScheduler.SchedulerResult
import com.dodo.dohabits.core.reminders.ReminderScheduler.SystemScheduler
import com.dodo.dohabits.core.utils.DateFormats
import com.dodo.dohabits.inject.AppContext
import java.util.Date
import javax.inject.Inject
import kotlin.math.min

@AppScope
class IntentScheduler
@Inject constructor(
    @AppContext context: Context,
    private val pendingIntents: PendingIntentFactory
) : SystemScheduler {

    private val manager =
        context.getSystemService(ALARM_SERVICE) as AlarmManager

    private fun schedule(timestamp: Long, intent: PendingIntent, alarmType: Int): SchedulerResult {
        val now = System.currentTimeMillis()
        Log.d("IntentScheduler", "timestamp=$timestamp now=$now")
        if (timestamp < now) {
            Log.e(
                "IntentScheduler",
                "Ignoring attempt to schedule intent in the past."
            )
            return SchedulerResult.IGNORED
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !manager.canScheduleExactAlarms()) {
            Log.e("IntentScheduler", "No permission to schedule exact alarms")
            return SchedulerResult.IGNORED
        }
        manager.setExactAndAllowWhileIdle(alarmType, timestamp, intent)
        return SchedulerResult.OK
    }

    override fun scheduleShowReminder(
        reminderTime: Long,
        habit: Habit,
        timestamp: Long
    ): SchedulerResult {
        val intent = pendingIntents.showReminder(habit, reminderTime, timestamp)
        logReminderScheduled(habit, reminderTime)
        return schedule(reminderTime, intent, RTC_WAKEUP)
    }

    override fun scheduleWidgetUpdate(updateTime: Long): SchedulerResult {
        val intent = pendingIntents.updateWidgets()
        return schedule(updateTime, intent, RTC)
    }

    override fun log(componentName: String, msg: String) {
        Log.d(componentName, msg)
    }

    private fun logReminderScheduled(habit: Habit, reminderTime: Long) {
        val min = min(5, habit.name.length)
        val name = habit.name.substring(0, min)
        val df = DateFormats.getBackupDateFormat()
        val time = df.format(Date(reminderTime))
        Log.i(
            "ReminderHelper",
            String.format("Setting alarm (%s): %s", time, name)
        )
    }
}
