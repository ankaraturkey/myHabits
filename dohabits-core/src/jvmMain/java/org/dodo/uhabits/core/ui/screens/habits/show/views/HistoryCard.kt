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

import org.dodo.platform.time.DayOfWeek
import org.dodo.platform.time.LocalDate
import com.dodo.dohabits.core.commands.CommandRunner
import com.dodo.dohabits.core.commands.CreateRepetitionCommand
import com.dodo.dohabits.core.models.Entry
import com.dodo.dohabits.core.models.Entry.Companion.SKIP
import com.dodo.dohabits.core.models.Entry.Companion.YES_AUTO
import com.dodo.dohabits.core.models.Entry.Companion.YES_MANUAL
import com.dodo.dohabits.core.models.Habit
import com.dodo.dohabits.core.models.HabitList
import com.dodo.dohabits.core.models.NumericalHabitType.AT_LEAST
import com.dodo.dohabits.core.models.NumericalHabitType.AT_MOST
import com.dodo.dohabits.core.models.PaletteColor
import com.dodo.dohabits.core.models.Timestamp
import com.dodo.dohabits.core.preferences.Preferences
import com.dodo.dohabits.core.ui.screens.habits.list.ListHabitsBehavior
import com.dodo.dohabits.core.ui.views.HistoryChart
import com.dodo.dohabits.core.ui.views.HistoryChart.Square.DIMMED
import com.dodo.dohabits.core.ui.views.HistoryChart.Square.GREY
import com.dodo.dohabits.core.ui.views.HistoryChart.Square.HATCHED
import com.dodo.dohabits.core.ui.views.HistoryChart.Square.OFF
import com.dodo.dohabits.core.ui.views.HistoryChart.Square.ON
import com.dodo.dohabits.core.ui.views.OnDateClickedListener
import com.dodo.dohabits.core.ui.views.Theme
import com.dodo.dohabits.core.utils.DateUtils
import kotlin.math.roundToInt

data class HistoryCardState(
    val color: PaletteColor,
    val firstWeekday: DayOfWeek,
    val series: List<HistoryChart.Square>,
    val defaultSquare: HistoryChart.Square,
    val notesIndicators: List<Boolean>,
    val theme: Theme,
    val today: LocalDate
)

class HistoryCardPresenter(
    val commandRunner: CommandRunner,
    val habit: Habit,
    val habitList: HabitList,
    val preferences: Preferences,
    val screen: Screen
) : OnDateClickedListener {

    override fun onDateLongPress(date: LocalDate) {
        val timestamp = Timestamp.fromLocalDate(date)
        screen.showFeedback()
        if (habit.isNumerical) {
            showNumberPopup(timestamp)
        } else {
            if (preferences.isShortToggleEnabled) {
                showCheckmarkPopup(timestamp)
            } else {
                toggle(timestamp)
            }
        }
    }

    override fun onDateShortPress(date: LocalDate) {
        val timestamp = Timestamp.fromLocalDate(date)
        screen.showFeedback()
        if (habit.isNumerical) {
            showNumberPopup(timestamp)
        } else {
            if (preferences.isShortToggleEnabled) {
                toggle(timestamp)
            } else {
                showCheckmarkPopup(timestamp)
            }
        }
    }

    private fun showCheckmarkPopup(timestamp: Timestamp) {
        val entry = habit.computedEntries.get(timestamp)
        screen.showCheckmarkPopup(
            entry.value,
            entry.notes,
            habit.color
        ) { newValue, newNotes ->
            commandRunner.run(
                CreateRepetitionCommand(
                    habitList,
                    habit,
                    timestamp,
                    newValue,
                    newNotes
                )
            )
        }
    }

    private fun toggle(timestamp: Timestamp) {
        val entry = habit.computedEntries.get(timestamp)
        val nextValue = Entry.nextToggleValue(
            value = entry.value,
            isSkipEnabled = preferences.isSkipEnabled,
            areQuestionMarksEnabled = preferences.areQuestionMarksEnabled
        )
        commandRunner.run(
            CreateRepetitionCommand(
                habitList,
                habit,
                timestamp,
                nextValue,
                entry.notes
            )
        )
    }

    private fun showNumberPopup(timestamp: Timestamp) {
        val entry = habit.computedEntries.get(timestamp)
        val oldValue = entry.value
        screen.showNumberPopup(
            value = oldValue / 1000.0,
            notes = entry.notes
        ) { newValue: Double, newNotes: String ->
            val thousands = (newValue * 1000).roundToInt()
            commandRunner.run(
                CreateRepetitionCommand(
                    habitList,
                    habit,
                    timestamp,
                    thousands,
                    newNotes
                )
            )
        }
    }

    fun onClickEditButton() {
        screen.showHistoryEditorDialog(this)
    }

    companion object {
        fun buildState(
            habit: Habit,
            firstWeekday: DayOfWeek,
            theme: Theme
        ): HistoryCardState {
            val today = DateUtils.getTodayWithOffset()
            val oldest = habit.computedEntries.getKnown().lastOrNull()?.timestamp ?: today
            val entries = habit.computedEntries.getByInterval(oldest, today)
            val series = if (habit.isNumerical) {
                entries.map {
                    when {
                        it.value == Entry.UNKNOWN -> OFF
                        it.value == SKIP -> HATCHED
                        (habit.targetType == AT_MOST) && (it.value / 1000.0 <= habit.targetValue) -> ON
                        (habit.targetType == AT_LEAST) && (it.value / 1000.0 >= habit.targetValue) -> ON
                        else -> GREY
                    }
                }
            } else {
                entries.map {
                    when (it.value) {
                        YES_MANUAL -> ON
                        YES_AUTO -> DIMMED
                        SKIP -> HATCHED
                        else -> OFF
                    }
                }
            }
            val notesIndicators = entries.map {
                when (it.notes) {
                    "" -> false
                    else -> true
                }
            }

            return HistoryCardState(
                color = habit.color,
                firstWeekday = firstWeekday,
                today = today.toLocalDate(),
                theme = theme,
                series = series,
                defaultSquare = OFF,
                notesIndicators = notesIndicators
            )
        }
    }

    interface Screen {
        fun showHistoryEditorDialog(listener: OnDateClickedListener)
        fun showFeedback()
        fun showNumberPopup(
            value: Double,
            notes: String,
            callback: ListHabitsBehavior.NumberPickerCallback
        )
        fun showCheckmarkPopup(
            selectedValue: Int,
            notes: String,
            color: PaletteColor,
            callback: ListHabitsBehavior.CheckMarkDialogCallback
        )
    }
}
