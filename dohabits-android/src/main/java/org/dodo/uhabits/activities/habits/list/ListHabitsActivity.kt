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

package com.dodo.dohabits.activities.habits.list

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import com.dodo.dohabits.BaseExceptionHandler
import com.dodo.dohabits.HabitsApplication
import com.dodo.dohabits.activities.habits.list.views.HabitCardListAdapter
import com.dodo.dohabits.core.models.Timestamp
import com.dodo.dohabits.core.preferences.Preferences
import com.dodo.dohabits.core.tasks.TaskRunner
import com.dodo.dohabits.core.ui.ThemeSwitcher.Companion.THEME_DARK
import com.dodo.dohabits.core.utils.MidnightTimer
import com.dodo.dohabits.database.AutoBackup
import com.dodo.dohabits.inject.ActivityContextModule
import com.dodo.dohabits.inject.DaggerHabitsActivityComponent
import com.dodo.dohabits.inject.HabitsActivityComponent
import com.dodo.dohabits.inject.HabitsApplicationComponent
import com.dodo.dohabits.utils.applyRootViewInsets
import com.dodo.dohabits.utils.dismissCurrentDialog
import com.dodo.dohabits.utils.restartWithFade

class ListHabitsActivity : AppCompatActivity(), Preferences.Listener {

    var pureBlack: Boolean = false
    lateinit var appComponent: HabitsApplicationComponent
    lateinit var component: HabitsActivityComponent
    lateinit var taskRunner: TaskRunner
    lateinit var adapter: HabitCardListAdapter
    lateinit var rootView: ListHabitsRootView
    lateinit var screen: ListHabitsScreen
    lateinit var prefs: Preferences
    lateinit var midnightTimer: MidnightTimer
    private val scope = CoroutineScope(Dispatchers.Main)

    private var permissionAlreadyRequested = false
    private val permissionLauncher =
        registerForActivityResult(RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                scheduleReminders()
            } else {
                Log.i("ListHabitsActivity", "POST_NOTIFICATIONS denied")
            }
        }

    private lateinit var menu: ListHabitsMenu

    override fun onQuestionMarksChanged() {
        invalidateOptionsMenu()
        menu.behavior.onPreferencesChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appComponent = (applicationContext as HabitsApplication).component
        component = DaggerHabitsActivityComponent
            .builder()
            .activityContextModule(ActivityContextModule(this))
            .habitsApplicationComponent(appComponent)
            .build()
        component.themeSwitcher.apply()

        prefs = appComponent.preferences
        prefs.addListener(this)
        pureBlack = prefs.isPureBlackEnabled
        midnightTimer = appComponent.midnightTimer
        rootView = component.listHabitsRootView
        screen = component.listHabitsScreen
        adapter = component.habitCardListAdapter
        taskRunner = appComponent.taskRunner
        menu = component.listHabitsMenu
        Thread.setDefaultUncaughtExceptionHandler(BaseExceptionHandler(this))
        component.listHabitsBehavior.onStartup()
        rootView.applyRootViewInsets()
        setContentView(rootView)
    }

    override fun onPause() {
        midnightTimer.onPause()
        screen.onDetached()
        adapter.cancelRefresh()
        dismissCurrentDialog()
        super.onPause()
    }

    override fun onResume() {
        adapter.refresh()
        screen.onAttached()
        rootView.postInvalidate()
        midnightTimer.onResume()

        if (appComponent.reminderScheduler.hasHabitsWithReminders()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                scheduleReminders()
            } else {
                if (checkSelfPermission(this, POST_NOTIFICATIONS) == PERMISSION_GRANTED) {
                    scheduleReminders()
                } else {
                    // If we have not requested the permission yet, request it. Otherwide do
                    // nothing. This check is necessary to avoid an infinite onResume loop in case
                    // the user denies the permission.
                    if (!permissionAlreadyRequested) {
                        Log.i("ListHabitsActivity", "Requestion permission: POST_NOTIFICATIONS")
                        permissionLauncher.launch(POST_NOTIFICATIONS)
                        permissionAlreadyRequested = true
                    }
                }
            }
        }

        taskRunner.run {
            try {
                AutoBackup(this@ListHabitsActivity).run()
                appComponent.widgetUpdater.updateWidgets()
            } catch (e: Exception) {
                Log.e("ListHabitActivity", "TaskRunner failed", e)
            }
        }
        if (prefs.theme == THEME_DARK && prefs.isPureBlackEnabled != pureBlack) {
            restartWithFade(ListHabitsActivity::class.java)
        }
        parseIntents()
        super.onResume()
    }

    private fun scheduleReminders() {
        appComponent.reminderScheduler.scheduleAll()
    }

    override fun onCreateOptionsMenu(m: Menu): Boolean {
        menu.onCreate(menuInflater, m)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        invalidateOptionsMenu()
        return menu.onItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(request: Int, result: Int, data: Intent?) {
        super.onActivityResult(request, result, data)
        screen.onResult(request, result, data)
    }

    private fun parseIntents() {
        if (intent == null) return
        if (intent.action == ACTION_EDIT) {
            val habitId = intent.extras?.getLong("habit")
            val timestamp = intent.extras?.getLong("timestamp")
            if (habitId != null && timestamp != null) {
                val habit = appComponent.habitList.getById(habitId)!!
                component.listHabitsBehavior.onEdit(habit, Timestamp(timestamp), 0f, 0f)
            }
        }
        intent = null
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    companion object {
        const val ACTION_EDIT = "com.dodo.dohabits.ACTION_EDIT"
    }
}
