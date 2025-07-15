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

package com.dodo.dohabits.widgets

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.dodo.dohabits.core.commands.Command
import com.dodo.dohabits.core.commands.CommandRunner
import com.dodo.dohabits.core.commands.CreateRepetitionCommand
import com.dodo.dohabits.core.preferences.WidgetPreferences
import com.dodo.dohabits.core.tasks.TaskRunner
import com.dodo.dohabits.core.utils.DateUtils
import com.dodo.dohabits.inject.AppContext
import com.dodo.dohabits.intents.IntentScheduler
import javax.inject.Inject

/**
 * A WidgetUpdater listens to the commands being executed by the application and
 * updates the home-screen widgets accordingly.
 */
class WidgetUpdater
@Inject constructor(
    @AppContext private val context: Context,
    private val commandRunner: CommandRunner,
    private val taskRunner: TaskRunner,
    private val widgetPrefs: WidgetPreferences,
    private val intentScheduler: IntentScheduler
) : CommandRunner.Listener {

    override fun onCommandFinished(command: Command) {
        if (command is CreateRepetitionCommand) {
            updateWidgets(command.habit.id)
        } else {
            updateWidgets()
        }
    }

    /**
     * Instructs the updater to start listening to commands. If any relevant
     * commands are executed after this method is called, the corresponding
     * widgets will get updated.
     */
    fun startListening() {
        commandRunner.addListener(this)
    }

    /**
     * Instructs the updater to stop listening to commands. Every command
     * executed after this method is called will be ignored by the updater.
     */
    fun stopListening() {
        commandRunner.removeListener(this)
    }

    fun scheduleStartDayWidgetUpdate() {
        val timestamp = DateUtils.getStartOfTomorrowWithOffset()
        intentScheduler.scheduleWidgetUpdate(timestamp)
    }

    fun updateWidgets(modifiedHabitId: Long?) {
        taskRunner.execute {
            updateWidgets(modifiedHabitId, CheckmarkWidgetProvider::class.java)
            updateWidgets(modifiedHabitId, HistoryWidgetProvider::class.java)
            updateWidgets(modifiedHabitId, ScoreWidgetProvider::class.java)
            updateWidgets(modifiedHabitId, StreakWidgetProvider::class.java)
            updateWidgets(modifiedHabitId, FrequencyWidgetProvider::class.java)
            updateWidgets(modifiedHabitId, TargetWidgetProvider::class.java)
        }
    }

    private fun updateWidgets(modifiedHabitId: Long?, providerClass: Class<*>) {
        val widgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(
            ComponentName(context, providerClass)
        )

        val modifiedWidgetIds = when (modifiedHabitId) {
            null -> widgetIds.toList()
            else -> widgetIds.filter { w ->
                widgetPrefs.getHabitIdsFromWidgetId(w).contains(modifiedHabitId)
            }
        }

        context.sendBroadcast(
            Intent(context, providerClass).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, modifiedWidgetIds.toIntArray())
            }
        )
    }

    fun updateWidgets() {
        updateWidgets(null)
    }
}
