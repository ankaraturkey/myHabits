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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.Component
import com.dodo.dohabits.HabitsApplication
import com.dodo.dohabits.core.ui.widgets.WidgetBehavior
import com.dodo.dohabits.inject.HabitsApplicationComponent
import com.dodo.dohabits.intents.IntentParser.CheckmarkIntentData

/**
 * The Android BroadcastReceiver for Daily Loop Tracker.
 *
 *
 * All broadcast messages are received and processed by this class.
 */
class WidgetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val app = context.applicationContext as HabitsApplication
        val component = DaggerWidgetReceiver_WidgetComponent
            .builder()
            .habitsApplicationComponent(app.component)
            .build()
        val parser = app.component.intentParser
        val controller = component.widgetController
        val prefs = app.component.preferences
        val widgetUpdater = app.component.widgetUpdater
        Log.i(TAG, String.format("Received intent: %s", intent.toString()))
        lastReceivedIntent = intent
        try {
            var data: CheckmarkIntentData? = null
            if (intent.action !== ACTION_UPDATE_WIDGETS_VALUE) {
                data = parser.parseCheckmarkIntent(intent)
            }
            when (intent.action) {
                ACTION_ADD_REPETITION -> {
                    Log.d(
                        TAG,
                        String.format(
                            "onAddRepetition habit=%d timestamp=%d",
                            data!!.habit.id,
                            data.timestamp.unixTime
                        )
                    )
                    controller.onAddRepetition(
                        data.habit,
                        data.timestamp
                    )
                }
                ACTION_TOGGLE_REPETITION -> {
                    Log.d(
                        TAG,
                        String.format(
                            "onToggleRepetition habit=%d timestamp=%d",
                            data!!.habit.id,
                            data.timestamp.unixTime
                        )
                    )
                    controller.onToggleRepetition(
                        data.habit,
                        data.timestamp
                    )
                }
                ACTION_REMOVE_REPETITION -> {
                    Log.d(
                        TAG,
                        String.format(
                            "onRemoveRepetition habit=%d timestamp=%d",
                            data!!.habit.id,
                            data.timestamp.unixTime
                        )
                    )
                    controller.onRemoveRepetition(
                        data.habit,
                        data.timestamp
                    )
                }
                ACTION_UPDATE_WIDGETS_VALUE -> {
                    widgetUpdater.updateWidgets()
                    widgetUpdater.scheduleStartDayWidgetUpdate()
                }
            }
        } catch (e: RuntimeException) {
            Log.e("WidgetReceiver", "could not process intent", e)
        }
    }

    @ReceiverScope
    @Component(dependencies = [HabitsApplicationComponent::class])
    internal interface WidgetComponent {
        val widgetController: WidgetBehavior
    }

    companion object {
        const val ACTION_ADD_REPETITION = "com.dodo.dohabits.ACTION_ADD_REPETITION"
        const val ACTION_DISMISS_REMINDER = "com.dodo.dohabits.ACTION_DISMISS_REMINDER"
        const val ACTION_REMOVE_REPETITION = "com.dodo.dohabits.ACTION_REMOVE_REPETITION"
        const val ACTION_TOGGLE_REPETITION = "com.dodo.dohabits.ACTION_TOGGLE_REPETITION"
        const val ACTION_UPDATE_WIDGETS_VALUE = "com.dodo.dohabits.ACTION_UPDATE_WIDGETS_VALUE"
        private const val TAG = "WidgetReceiver"
        var lastReceivedIntent: Intent? = null
            private set

        fun clearLastReceivedIntent() {
            lastReceivedIntent = null
        }
    }
}
