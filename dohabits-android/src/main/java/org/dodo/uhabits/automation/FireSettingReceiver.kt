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

package com.dodo.dohabits.automation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.Component
import com.dodo.dohabits.HabitsApplication
import com.dodo.dohabits.core.models.HabitList
import com.dodo.dohabits.core.ui.widgets.WidgetBehavior
import com.dodo.dohabits.core.utils.DateUtils
import com.dodo.dohabits.inject.HabitsApplicationComponent
import com.dodo.dohabits.receivers.ReceiverScope

const val ACTION_CHECK = 0
const val ACTION_UNCHECK = 1
const val ACTION_TOGGLE = 2
const val ACTION_INCREMENT = 3
const val ACTION_DECREMENT = 4

const val EXTRA_BUNDLE = "com.twofortyfouram.locale.intent.extra.BUNDLE"
const val EXTRA_STRING_BLURB = "com.twofortyfouram.locale.intent.extra.BLURB"

class FireSettingReceiver : BroadcastReceiver() {

    private lateinit var allHabits: HabitList

    override fun onReceive(context: Context, intent: Intent) {
        val app = context.applicationContext as HabitsApplication
        val component = DaggerFireSettingReceiver_ReceiverComponent
            .builder()
            .habitsApplicationComponent(app.component)
            .build()
        allHabits = app.component.habitList
        val args = SettingUtils.parseIntent(intent, allHabits) ?: return
        val timestamp = DateUtils.getTodayWithOffset()
        val controller = component.widgetController

        when (args.action) {
            ACTION_CHECK -> controller.onAddRepetition(args.habit, timestamp)
            ACTION_UNCHECK -> controller.onRemoveRepetition(args.habit, timestamp)
            ACTION_TOGGLE -> controller.onToggleRepetition(args.habit, timestamp)
            ACTION_INCREMENT -> controller.onIncrement(args.habit, timestamp, 1000)
            ACTION_DECREMENT -> controller.onDecrement(args.habit, timestamp, 1000)
        }
    }

    @ReceiverScope
    @Component(dependencies = [HabitsApplicationComponent::class])
    internal interface ReceiverComponent {
        val widgetController: WidgetBehavior
    }
}
