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

import dagger.Component
import com.dodo.dohabits.activities.common.dialogs.ColorPickerDialogFactory
import com.dodo.dohabits.activities.habits.list.ListHabitsMenu
import com.dodo.dohabits.activities.habits.list.ListHabitsModule
import com.dodo.dohabits.activities.habits.list.ListHabitsRootView
import com.dodo.dohabits.activities.habits.list.ListHabitsScreen
import com.dodo.dohabits.activities.habits.list.ListHabitsSelectionMenu
import com.dodo.dohabits.activities.habits.list.views.HabitCardListAdapter
import com.dodo.dohabits.core.ui.ThemeSwitcher
import com.dodo.dohabits.core.ui.screens.habits.list.ListHabitsBehavior

@ActivityScope
@Component(
    modules = [ActivityContextModule::class, HabitsActivityModule::class, ListHabitsModule::class, HabitModule::class],
    dependencies = [HabitsApplicationComponent::class]
)
interface HabitsActivityComponent {
    val colorPickerDialogFactory: ColorPickerDialogFactory
    val habitCardListAdapter: HabitCardListAdapter
    val listHabitsBehavior: ListHabitsBehavior
    val listHabitsMenu: ListHabitsMenu
    val listHabitsRootView: ListHabitsRootView
    val listHabitsScreen: ListHabitsScreen
    val listHabitsSelectionMenu: ListHabitsSelectionMenu
    val themeSwitcher: ThemeSwitcher
}
