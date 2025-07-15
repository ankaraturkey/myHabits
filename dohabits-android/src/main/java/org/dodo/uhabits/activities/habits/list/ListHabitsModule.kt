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
import dagger.Binds
import dagger.Module
import com.dodo.dohabits.AndroidBugReporter
import com.dodo.dohabits.activities.HabitsDirFinder
import com.dodo.dohabits.activities.habits.list.views.HabitCardListAdapter
import com.dodo.dohabits.core.ui.screens.habits.list.ListHabitsBehavior
import com.dodo.dohabits.core.ui.screens.habits.list.ListHabitsMenuBehavior
import com.dodo.dohabits.core.ui.screens.habits.list.ListHabitsSelectionMenuBehavior
import com.dodo.dohabits.inject.AppContext
import javax.inject.Inject

class BugReporterProxy
@Inject constructor(
    @AppContext context: Context
) : AndroidBugReporter(context), ListHabitsBehavior.BugReporter

@Module
abstract class ListHabitsModule {

    @Binds
    abstract fun getAdapter(adapter: HabitCardListAdapter): ListHabitsMenuBehavior.Adapter

    @Binds
    abstract fun getBugReporter(proxy: BugReporterProxy): ListHabitsBehavior.BugReporter

    @Binds
    abstract fun getMenuScreen(screen: ListHabitsScreen): ListHabitsMenuBehavior.Screen

    @Binds
    abstract fun getScreen(screen: ListHabitsScreen): ListHabitsBehavior.Screen

    @Binds
    abstract fun getSelMenuAdapter(adapter: HabitCardListAdapter): ListHabitsSelectionMenuBehavior.Adapter

    @Binds
    abstract fun getSelMenuScreen(screen: ListHabitsScreen): ListHabitsSelectionMenuBehavior.Screen

    @Binds
    abstract fun getSystem(system: HabitsDirFinder): ListHabitsBehavior.DirFinder
}
