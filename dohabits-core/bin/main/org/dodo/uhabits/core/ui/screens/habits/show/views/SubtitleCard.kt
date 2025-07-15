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

package com.dodo.dohabits.core.ui.screens.habits.show.views

import com.dodo.dohabits.core.models.Frequency
import com.dodo.dohabits.core.models.Habit
import com.dodo.dohabits.core.models.NumericalHabitType
import com.dodo.dohabits.core.models.PaletteColor
import com.dodo.dohabits.core.models.Reminder
import com.dodo.dohabits.core.ui.views.Theme

data class SubtitleCardState(
    val color: PaletteColor,
    val frequency: Frequency,
    val isNumerical: Boolean,
    val question: String,
    val reminder: Reminder?,
    val targetValue: Double = 0.0,
    val targetType: NumericalHabitType = NumericalHabitType.AT_LEAST,
    val unit: String = "",
    val theme: Theme
)

class SubtitleCardPresenter {
    companion object {
        fun buildState(
            habit: Habit,
            theme: Theme
        ): SubtitleCardState = SubtitleCardState(
            color = habit.color,
            frequency = habit.frequency,
            isNumerical = habit.isNumerical,
            question = habit.question,
            reminder = habit.reminder,
            targetValue = habit.targetValue,
            targetType = habit.targetType,
            unit = habit.unit,
            theme = theme
        )
    }
}
