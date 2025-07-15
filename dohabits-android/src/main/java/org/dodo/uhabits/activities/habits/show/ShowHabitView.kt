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

package com.dodo.dohabits.activities.habits.show

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.dodo.dohabits.core.ui.screens.habits.show.ShowHabitPresenter
import com.dodo.dohabits.core.ui.screens.habits.show.ShowHabitState
import com.dodo.dohabits.databinding.ShowHabitBinding
import com.dodo.dohabits.utils.applyBottomInset
import com.dodo.dohabits.utils.applyToolbarInsets
import com.dodo.dohabits.utils.setupToolbar

class ShowHabitView(context: Context) : FrameLayout(context) {
    private val binding = ShowHabitBinding.inflate(LayoutInflater.from(context))

    init {
        binding.toolbar.applyToolbarInsets()
        addView(binding.root)
    }

    fun setState(data: ShowHabitState) {
        setupToolbar(
            binding.toolbar,
            title = data.title,
            color = data.color,
            theme = data.theme
        )
        binding.subtitleCard.setState(data.subtitle)
        binding.overviewCard.setState(data.overview)
        binding.notesCard.setState(data.notes)
        binding.targetCard.setState(data.target)
        binding.streakCard.setState(data.streaks)
        binding.scoreCard.setState(data.scores)
        binding.frequencyCard.setState(data.frequency)
        binding.historyCard.setState(data.history)
        binding.barCard.setState(data.bar)
        if (data.isNumerical) {
            binding.overviewCard.visibility = GONE
        } else {
            binding.targetCard.visibility = GONE
        }
        binding.linearLayout.applyBottomInset()
    }

    fun setListener(presenter: ShowHabitPresenter) {
        binding.scoreCard.setListener(presenter.scoreCardPresenter)
        binding.historyCard.setListener(presenter.historyCardPresenter)
        binding.barCard.setListener(presenter.barCardPresenter)
    }
}
