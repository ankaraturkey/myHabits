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
package com.dodo.dohabits.inject

import android.content.Context
import dagger.Component
import com.dodo.dohabits.core.AppScope
import com.dodo.dohabits.core.commands.CommandRunner
import com.dodo.dohabits.core.io.GenericImporter
import com.dodo.dohabits.core.io.Logging
import com.dodo.dohabits.core.models.HabitList
import com.dodo.dohabits.core.models.ModelFactory
import com.dodo.dohabits.core.preferences.Preferences
import com.dodo.dohabits.core.preferences.WidgetPreferences
import com.dodo.dohabits.core.reminders.ReminderScheduler
import com.dodo.dohabits.core.tasks.TaskRunner
import com.dodo.dohabits.core.ui.NotificationTray
import com.dodo.dohabits.core.ui.screens.habits.list.HabitCardListCache
import com.dodo.dohabits.core.utils.MidnightTimer
import com.dodo.dohabits.intents.IntentFactory
import com.dodo.dohabits.intents.IntentParser
import com.dodo.dohabits.intents.PendingIntentFactory
import com.dodo.dohabits.receivers.ReminderController
import com.dodo.dohabits.tasks.AndroidTaskRunner
import com.dodo.dohabits.widgets.WidgetUpdater

@AppScope
@Component(modules = [AppContextModule::class, HabitsModule::class, AndroidTaskRunner::class])
interface HabitsApplicationComponent {
    val commandRunner: CommandRunner

    @get:AppContext
    val context: Context
    val genericImporter: GenericImporter
    val habitCardListCache: HabitCardListCache
    val habitList: HabitList
    val intentFactory: IntentFactory
    val intentParser: IntentParser
    val logging: Logging
    val midnightTimer: MidnightTimer
    val modelFactory: ModelFactory
    val notificationTray: NotificationTray
    val pendingIntentFactory: PendingIntentFactory
    val preferences: Preferences
    val reminderScheduler: ReminderScheduler
    val reminderController: ReminderController
    val taskRunner: TaskRunner
    val widgetPreferences: WidgetPreferences
    val widgetUpdater: WidgetUpdater
}
