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

import dagger.Module
import dagger.Provides
import com.dodo.dohabits.core.AppScope
import com.dodo.dohabits.core.commands.CommandRunner
import com.dodo.dohabits.core.database.Database
import com.dodo.dohabits.core.database.DatabaseOpener
import com.dodo.dohabits.core.io.Logging
import com.dodo.dohabits.core.models.HabitList
import com.dodo.dohabits.core.models.ModelFactory
import com.dodo.dohabits.core.models.sqlite.SQLModelFactory
import com.dodo.dohabits.core.models.sqlite.SQLiteHabitList
import com.dodo.dohabits.core.preferences.Preferences
import com.dodo.dohabits.core.preferences.WidgetPreferences
import com.dodo.dohabits.core.reminders.ReminderScheduler
import com.dodo.dohabits.core.tasks.TaskRunner
import com.dodo.dohabits.core.ui.NotificationTray
import com.dodo.dohabits.database.AndroidDatabase
import com.dodo.dohabits.database.AndroidDatabaseOpener
import com.dodo.dohabits.intents.IntentScheduler
import com.dodo.dohabits.io.AndroidLogging
import com.dodo.dohabits.notifications.AndroidNotificationTray
import com.dodo.dohabits.preferences.SharedPreferencesStorage
import com.dodo.dohabits.utils.DatabaseUtils
import java.io.File

@Module
class HabitsModule(dbFile: File) {

    val db: Database = AndroidDatabase(DatabaseUtils.openDatabase(), dbFile)

    @Provides
    @AppScope
    fun getPreferences(storage: SharedPreferencesStorage): Preferences {
        return Preferences(storage)
    }

    @Provides
    @AppScope
    fun getReminderScheduler(
        sys: IntentScheduler,
        commandRunner: CommandRunner,
        habitList: HabitList,
        widgetPreferences: WidgetPreferences
    ): ReminderScheduler {
        return ReminderScheduler(commandRunner, habitList, sys, widgetPreferences)
    }

    @Provides
    @AppScope
    fun getTray(
        taskRunner: TaskRunner,
        commandRunner: CommandRunner,
        preferences: Preferences,
        screen: AndroidNotificationTray
    ): NotificationTray {
        return NotificationTray(taskRunner, commandRunner, preferences, screen)
    }

    @Provides
    @AppScope
    fun getWidgetPreferences(
        storage: SharedPreferencesStorage
    ): WidgetPreferences {
        return WidgetPreferences(storage)
    }

    @Provides
    @AppScope
    fun getModelFactory(): ModelFactory {
        return SQLModelFactory(db)
    }

    @Provides
    @AppScope
    fun getHabitList(list: SQLiteHabitList): HabitList {
        return list
    }

    @Provides
    @AppScope
    fun getDatabaseOpener(opener: AndroidDatabaseOpener): DatabaseOpener {
        return opener
    }

    @Provides
    @AppScope
    fun getLogging(): Logging {
        return AndroidLogging()
    }

    @Provides
    @AppScope
    fun getDatabase(): Database {
        return db
    }
}
