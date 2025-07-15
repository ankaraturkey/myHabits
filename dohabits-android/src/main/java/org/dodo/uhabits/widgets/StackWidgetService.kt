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
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.RemoteViewsService.RemoteViewsFactory
import org.dodo.platform.utils.StringUtils.Companion.splitLongs
import com.dodo.dohabits.HabitsApplication
import com.dodo.dohabits.R
import com.dodo.dohabits.core.models.Habit
import com.dodo.dohabits.core.models.HabitNotFoundException
import com.dodo.dohabits.core.preferences.Preferences
import com.dodo.dohabits.core.utils.DateUtils.Companion.getTodayWithOffset
import com.dodo.dohabits.intents.IntentFactory
import com.dodo.dohabits.intents.PendingIntentFactory
import com.dodo.dohabits.utils.InterfaceUtils.dpToPixels

class StackWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return StackRemoteViewsFactory(this.applicationContext, intent)
    }

    companion object {
        const val WIDGET_TYPE = "WIDGET_TYPE"
        const val HABIT_IDS = "HABIT_IDS"
    }
}

internal class StackRemoteViewsFactory(private val context: Context, intent: Intent) :
    RemoteViewsFactory {
    private val widgetId: Int = intent.getIntExtra(
        AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID
    )
    private val habitIds: LongArray
    private val widgetType: StackWidgetType
    override fun onCreate() {}
    override fun onDestroy() {}
    override fun getCount(): Int {
        return habitIds.size
    }

    fun getDimensionsFromOptions(
        ctx: Context,
        options: Bundle
    ): WidgetDimensions {
        val maxWidth = dpToPixels(
            ctx,
            options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH).toFloat()
        ).toInt()
        val maxHeight = dpToPixels(
            ctx,
            options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT).toFloat()
        ).toInt()
        val minWidth = dpToPixels(
            ctx,
            options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH).toFloat()
        ).toInt()
        val minHeight = dpToPixels(
            ctx,
            options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT).toFloat()
        ).toInt()
        return WidgetDimensions(minWidth, maxHeight, maxWidth, minHeight)
    }

    override fun getViewAt(position: Int): RemoteViews? {
        Log.i("StackRemoteViewsFactory", "getViewAt $position started")
        if (position < 0 || position >= habitIds.size) return null
        val app = context.applicationContext as HabitsApplication
        val prefs = app.component.preferences
        val habitList = app.component.habitList
        val options = AppWidgetManager.getInstance(context).getAppWidgetOptions(widgetId)
        if (Looper.myLooper() == null) Looper.prepare()
        val habits = habitIds.map { habitList.getById(it) ?: throw HabitNotFoundException() }
        val h = habits[position]
        val widget = constructWidget(h, prefs)
        widget.setDimensions(getDimensionsFromOptions(context, options))
        val landscapeViews = widget.landscapeRemoteViews
        val portraitViews = widget.portraitRemoteViews
        val factory = PendingIntentFactory(context, IntentFactory())
        val intent = StackWidgetType.getIntentFillIn(factory, widgetType, h, habits, getTodayWithOffset())
        landscapeViews.setOnClickFillInIntent(R.id.button, intent)
        portraitViews.setOnClickFillInIntent(R.id.button, intent)
        val remoteViews = RemoteViews(landscapeViews, portraitViews)
        Log.i("StackRemoteViewsFactory", "getViewAt $position ended")
        return remoteViews
    }

    private fun constructWidget(
        habit: Habit,
        prefs: Preferences
    ): BaseWidget {
        return when (widgetType) {
            StackWidgetType.CHECKMARK -> CheckmarkWidget(context, widgetId, habit, true)
            StackWidgetType.FREQUENCY -> FrequencyWidget(
                context,
                widgetId,
                habit,
                prefs.firstWeekdayInt,
                true
            )
            StackWidgetType.SCORE -> ScoreWidget(context, widgetId, habit, true)
            StackWidgetType.HISTORY -> HistoryWidget(context, widgetId, habit, true)
            StackWidgetType.STREAKS -> StreakWidget(context, widgetId, habit, true)
            StackWidgetType.TARGET -> TargetWidget(context, widgetId, habit, true)
        }
    }

    override fun getLoadingView(): RemoteViews {
        val options = AppWidgetManager.getInstance(context).getAppWidgetOptions(widgetId)
        val widget = EmptyWidget(context, widgetId)
        widget.setDimensions(getDimensionsFromOptions(context, options))
        val landscapeViews = widget.landscapeRemoteViews
        val portraitViews = widget.portraitRemoteViews
        return RemoteViews(landscapeViews, portraitViews)
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return habitIds[position]
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun onDataSetChanged() {
    }

    init {
        val widgetTypeValue = intent.getIntExtra(StackWidgetService.WIDGET_TYPE, -1)
        val habitIdsStr = intent.getStringExtra(StackWidgetService.HABIT_IDS)
        if (widgetTypeValue < 0) throw RuntimeException("invalid widget type")
        if (habitIdsStr == null) throw RuntimeException("habitIdsStr is null")
        widgetType = StackWidgetType.getWidgetTypeFromValue(widgetTypeValue)
            ?: throw RuntimeException("unknown widget type value: $widgetTypeValue")
        habitIds = splitLongs(habitIdsStr)
    }
}
