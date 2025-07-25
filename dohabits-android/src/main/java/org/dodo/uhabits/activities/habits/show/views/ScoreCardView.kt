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
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import org.dodo.platform.gui.toInt
import com.dodo.dohabits.core.ui.screens.habits.show.views.ScoreCardPresenter
import com.dodo.dohabits.core.ui.screens.habits.show.views.ScoreCardState
import com.dodo.dohabits.databinding.ShowHabitScoreBinding

class ScoreCardView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private var binding = ShowHabitScoreBinding.inflate(LayoutInflater.from(context), this)

    fun setState(state: ScoreCardState) {
        val androidColor = state.theme.color(state.color).toInt()
        binding.title.setTextColor(androidColor)
        binding.spinner.setSelection(state.spinnerPosition)
        binding.scoreView.setScores(state.scores)
        binding.scoreView.reset()
        binding.scoreView.setBucketSize(state.bucketSize)
        binding.scoreView.setColor(androidColor)
    }

    fun setListener(presenter: ScoreCardPresenter) {
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                presenter.onSpinnerPosition(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }
}
