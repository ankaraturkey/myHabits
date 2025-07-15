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

import android.content.Context
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import dagger.Lazy
import com.dodo.dohabits.R
import com.dodo.dohabits.activities.habits.list.views.HabitCardListAdapter
import com.dodo.dohabits.activities.habits.list.views.HabitCardListController
import com.dodo.dohabits.core.commands.CommandRunner
import com.dodo.dohabits.core.preferences.Preferences
import com.dodo.dohabits.core.ui.NotificationTray
import com.dodo.dohabits.core.ui.screens.habits.list.ListHabitsSelectionMenuBehavior
import com.dodo.dohabits.core.utils.DateUtils
import com.dodo.dohabits.inject.ActivityContext
import com.dodo.dohabits.inject.ActivityScope
import javax.inject.Inject

@ActivityScope
class ListHabitsSelectionMenu @Inject constructor(
    @ActivityContext context: Context,
    private val listAdapter: HabitCardListAdapter,
    var commandRunner: CommandRunner,
    private val prefs: Preferences,
    private val behavior: ListHabitsSelectionMenuBehavior,
    private val listController: Lazy<HabitCardListController>,
    private val notificationTray: NotificationTray
) : ActionMode.Callback {

    val activity = (context as AppCompatActivity)

    var activeActionMode: ActionMode? = null

    fun onSelectionStart() {
        activity.startSupportActionMode(this)
    }

    fun onSelectionChange() {
        activeActionMode?.invalidate()
    }

    fun onSelectionFinish() {
        activeActionMode?.finish()
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        activeActionMode = mode
        activity.menuInflater.inflate(R.menu.list_habits_selection, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        val itemEdit = menu.findItem(R.id.action_edit_habit)
        val itemColor = menu.findItem(R.id.action_color)
        val itemArchive = menu.findItem(R.id.action_archive_habit)
        val itemUnarchive = menu.findItem(R.id.action_unarchive_habit)
        val itemNotify = menu.findItem(R.id.action_notify)

        itemColor.isVisible = true
        itemEdit.isVisible = behavior.canEdit()
        itemArchive.isVisible = behavior.canArchive()
        itemUnarchive.isVisible = behavior.canUnarchive()
        itemNotify.isVisible = prefs.isDeveloper
        activeActionMode?.title = listAdapter.selected.size.toString()
        return true
    }
    override fun onDestroyActionMode(mode: ActionMode?) {
        listController.get().onSelectionFinished()
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit_habit -> {
                behavior.onEditHabits()
                return true
            }

            R.id.action_archive_habit -> {
                behavior.onArchiveHabits()
                return true
            }

            R.id.action_unarchive_habit -> {
                behavior.onUnarchiveHabits()
                return true
            }

            R.id.action_delete -> {
                behavior.onDeleteHabits()
                return true
            }

            R.id.action_color -> {
                behavior.onChangeColor()
                return true
            }

            R.id.action_notify -> {
                for (h in listAdapter.selected)
                    notificationTray.show(h, DateUtils.getToday(), 0)
                return true
            }

            else -> return false
        }
    }
}
