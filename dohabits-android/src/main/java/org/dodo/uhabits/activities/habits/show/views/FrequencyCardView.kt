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
package com.dodo.dohabits.activities.habits.show.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import org.dodo.platform.gui.toInt
import com.dodo.dohabits.core.ui.screens.habits.show.views.FrequencyCardState
import com.dodo.dohabits.databinding.ShowHabitFrequencyBinding

class FrequencyCardView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private var binding = ShowHabitFrequencyBinding.inflate(LayoutInflater.from(context), this)

    fun setState(state: FrequencyCardState) {
        val androidColor = state.theme.color(state.color).toInt()
        binding.frequencyChart.setFrequency(state.frequency)
        binding.frequencyChart.setIsNumerical(state.isNumerical)
        binding.frequencyChart.setFirstWeekday(state.firstWeekday)
        binding.title.setTextColor(androidColor)
        binding.frequencyChart.setColor(androidColor)
    }
}
