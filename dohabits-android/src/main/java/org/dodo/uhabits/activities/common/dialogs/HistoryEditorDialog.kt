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
package com.dodo.dohabits.activities.common.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import org.dodo.platform.gui.AndroidDataView
import org.dodo.platform.time.JavaLocalDateFormatter
import com.dodo.dohabits.HabitsApplication
import com.dodo.dohabits.R
import com.dodo.dohabits.activities.AndroidThemeSwitcher
import com.dodo.dohabits.core.commands.Command
import com.dodo.dohabits.core.commands.CommandRunner
import com.dodo.dohabits.core.models.Habit
import com.dodo.dohabits.core.preferences.Preferences
import com.dodo.dohabits.core.ui.screens.habits.show.views.HistoryCardPresenter
import com.dodo.dohabits.core.ui.views.HistoryChart
import com.dodo.dohabits.core.ui.views.LightTheme
import com.dodo.dohabits.core.ui.views.OnDateClickedListener
import com.dodo.dohabits.core.utils.DateUtils
import java.util.Locale
import kotlin.math.min

class HistoryEditorDialog : AppCompatDialogFragment(), CommandRunner.Listener {

    private lateinit var commandRunner: CommandRunner
    private lateinit var habit: Habit
    private lateinit var preferences: Preferences
    lateinit var dataView: AndroidDataView

    private var chart: HistoryChart? = null
    private var onDateClickedListener: OnDateClickedListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        clearCurrentDialog()
        val component = (requireActivity().application as HabitsApplication).component
        commandRunner = component.commandRunner
        habit = component.habitList.getById(requireArguments().getLong("habit"))!!
        preferences = component.preferences

        val themeSwitcher = AndroidThemeSwitcher(requireActivity(), preferences)
        themeSwitcher.apply()

        chart = HistoryChart(
            dateFormatter = JavaLocalDateFormatter(Locale.getDefault()),
            firstWeekday = preferences.firstWeekday,
            paletteColor = habit.color,
            series = emptyList(),
            defaultSquare = HistoryChart.Square.OFF,
            notesIndicators = emptyList(),
            theme = themeSwitcher.currentTheme,
            today = DateUtils.getTodayWithOffset().toLocalDate(),
            onDateClickedListener = onDateClickedListener ?: object : OnDateClickedListener {},
            padding = 10.0
        )
        dataView = AndroidDataView(requireContext(), null)
        dataView.view = chart!!

        val dialog = Dialog(requireContext()).apply {
            val metrics = resources.displayMetrics
            val maxHeight = resources.getDimensionPixelSize(R.dimen.history_editor_max_height)
            setContentView(dataView)
            window!!.setLayout(metrics.widthPixels, min(metrics.heightPixels, maxHeight))
        }

        currentDialog = dialog
        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        currentDialog = null
    }

    override fun onResume() {
        super.onResume()
        commandRunner.addListener(this)
        refreshData()
    }

    override fun onPause() {
        commandRunner.removeListener(this)
        super.onPause()
    }

    fun setOnDateClickedListener(listener: OnDateClickedListener) {
        onDateClickedListener = listener
        chart?.onDateClickedListener = listener
    }

    private fun refreshData() {
        val model = HistoryCardPresenter.buildState(
            habit,
            preferences.firstWeekday,
            theme = LightTheme()
        )
        chart?.series = model.series
        chart?.defaultSquare = model.defaultSquare
        chart?.notesIndicators = model.notesIndicators
        dataView.postInvalidate()
    }

    override fun onCommandFinished(command: Command) {
        refreshData()
    }

    companion object {
        // HistoryEditorDialog handles multiple dialogs on its own,
        // because sometimes we want it to be shown under another dialog (e.g. NumberPopup)
        var currentDialog: Dialog? = null
        fun clearCurrentDialog() {
            currentDialog?.dismiss()
            currentDialog = null
        }
    }
}
