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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import dagger.Lazy
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import org.dodo.platform.gui.toInt
import com.dodo.dohabits.R
import com.dodo.dohabits.activities.common.dialogs.CheckmarkDialog
import com.dodo.dohabits.activities.common.dialogs.ColorPickerDialogFactory
import com.dodo.dohabits.activities.common.dialogs.ConfirmDeleteDialog
import com.dodo.dohabits.activities.common.dialogs.NumberDialog
import com.dodo.dohabits.activities.habits.edit.HabitTypeDialog
import com.dodo.dohabits.activities.habits.list.views.HabitCardListAdapter
import com.dodo.dohabits.core.commands.ArchiveHabitsCommand
import com.dodo.dohabits.core.commands.ChangeHabitColorCommand
import com.dodo.dohabits.core.commands.Command
import com.dodo.dohabits.core.commands.CommandRunner
import com.dodo.dohabits.core.commands.CreateHabitCommand
import com.dodo.dohabits.core.commands.DeleteHabitsCommand
import com.dodo.dohabits.core.commands.EditHabitCommand
import com.dodo.dohabits.core.commands.UnarchiveHabitsCommand
import com.dodo.dohabits.core.models.Habit
import com.dodo.dohabits.core.models.PaletteColor
import com.dodo.dohabits.core.preferences.Preferences
import com.dodo.dohabits.core.tasks.TaskRunner
import com.dodo.dohabits.core.ui.ThemeSwitcher
import com.dodo.dohabits.core.ui.callbacks.OnColorPickedCallback
import com.dodo.dohabits.core.ui.callbacks.OnConfirmedCallback
import com.dodo.dohabits.core.ui.screens.habits.list.ListHabitsBehavior
import com.dodo.dohabits.core.ui.screens.habits.list.ListHabitsBehavior.Message.COULD_NOT_EXPORT
import com.dodo.dohabits.core.ui.screens.habits.list.ListHabitsBehavior.Message.COULD_NOT_GENERATE_BUG_REPORT
import com.dodo.dohabits.core.ui.screens.habits.list.ListHabitsBehavior.Message.DATABASE_REPAIRED
import com.dodo.dohabits.core.ui.screens.habits.list.ListHabitsBehavior.Message.FILE_NOT_RECOGNIZED
import com.dodo.dohabits.core.ui.screens.habits.list.ListHabitsBehavior.Message.IMPORT_FAILED
import com.dodo.dohabits.core.ui.screens.habits.list.ListHabitsBehavior.Message.IMPORT_SUCCESSFUL
import com.dodo.dohabits.core.ui.screens.habits.list.ListHabitsMenuBehavior
import com.dodo.dohabits.core.ui.screens.habits.list.ListHabitsSelectionMenuBehavior
import com.dodo.dohabits.inject.ActivityContext
import com.dodo.dohabits.inject.ActivityScope
import com.dodo.dohabits.intents.IntentFactory
import com.dodo.dohabits.tasks.ExportDBTaskFactory
import com.dodo.dohabits.tasks.ImportDataTask
import com.dodo.dohabits.tasks.ImportDataTaskFactory
import com.dodo.dohabits.utils.ColorUtils
import com.dodo.dohabits.utils.copyTo
import com.dodo.dohabits.utils.currentTheme
import com.dodo.dohabits.utils.dismissCurrentAndShow
import com.dodo.dohabits.utils.restartWithFade
import com.dodo.dohabits.utils.showMessage
import com.dodo.dohabits.utils.showSendEmailScreen
import com.dodo.dohabits.utils.showSendFileScreen
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

const val RESULT_IMPORT_DATA = 101
const val RESULT_EXPORT_CSV = 102
const val RESULT_EXPORT_DB = 103
const val RESULT_BUG_REPORT = 104
const val RESULT_REPAIR_DB = 105
const val REQUEST_OPEN_DOCUMENT = 106
const val REQUEST_SETTINGS = 107

@ActivityScope
class ListHabitsScreen
@Inject constructor(
    @ActivityContext val context: Context,
    private val commandRunner: CommandRunner,
    private val intentFactory: IntentFactory,
    private val themeSwitcher: ThemeSwitcher,
    private val adapter: HabitCardListAdapter,
    private val taskRunner: TaskRunner,
    private val exportDBFactory: ExportDBTaskFactory,
    private val importTaskFactory: ImportDataTaskFactory,
    private val colorPickerFactory: ColorPickerDialogFactory,
    private val behavior: Lazy<ListHabitsBehavior>,
    private val preferences: Preferences,
    private val rootView: Lazy<ListHabitsRootView>
) : CommandRunner.Listener,
    ListHabitsBehavior.Screen,
    ListHabitsMenuBehavior.Screen,
    ListHabitsSelectionMenuBehavior.Screen {

    val activity = (context as AppCompatActivity)

    fun onAttached() {
        commandRunner.addListener(this)
    }

    fun onDetached() {
        commandRunner.removeListener(this)
    }

    override fun onCommandFinished(command: Command) {
        val msg = getExecuteString(command)
        if (msg != null) activity.showMessage(msg)
    }

    fun onResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_OPEN_DOCUMENT -> onOpenDocumentResult(resultCode, data)
            REQUEST_SETTINGS -> onSettingsResult(resultCode)
        }
    }

    private fun onOpenDocumentResult(resultCode: Int, data: Intent?) {
        if (data == null) return
        if (resultCode != Activity.RESULT_OK) return
        try {
            val inStream = activity.contentResolver.openInputStream(data.data!!)!!
            val cacheDir = activity.externalCacheDir
            val tempFile = File.createTempFile("import", "", cacheDir)
            inStream.copyTo(tempFile)
            onImportData(tempFile) { tempFile.delete() }
        } catch (e: IOException) {
            activity.showMessage(activity.resources.getString(R.string.could_not_import))
            e.printStackTrace()
        }
    }

    private fun onSettingsResult(resultCode: Int) {
        when (resultCode) {
            RESULT_IMPORT_DATA -> showImportScreen()
            RESULT_EXPORT_CSV -> behavior.get().onExportCSV()
            RESULT_EXPORT_DB -> onExportDB()
            RESULT_BUG_REPORT -> behavior.get().onSendBugReport()
            RESULT_REPAIR_DB -> behavior.get().onRepairDB()
        }
    }

    override fun applyTheme() {
        themeSwitcher.apply()
        activity.restartWithFade(ListHabitsActivity::class.java)
    }

    override fun showSelectHabitTypeDialog() {
        val dialog = HabitTypeDialog()
        dialog.show(activity.supportFragmentManager, "habitType")
    }

    override fun showDeleteConfirmationScreen(callback: OnConfirmedCallback, quantity: Int) {
        ConfirmDeleteDialog(activity, callback, quantity).dismissCurrentAndShow()
    }

    override fun showEditHabitsScreen(selected: List<Habit>) {
        val intent = intentFactory.startEditActivity(activity, selected[0])
        activity.startActivity(intent)
    }

    override fun showFAQScreen() {
        val intent = intentFactory.viewFAQ(activity)
        activity.startActivity(intent)
    }

    override fun showHabitScreen(h: Habit) {
        val intent = intentFactory.startShowHabitActivity(activity, h)
        activity.startActivity(intent)
    }

    fun showImportScreen() {
        val intent = intentFactory.openDocument()
        activity.startActivityForResult(intent, REQUEST_OPEN_DOCUMENT)
    }

    override fun showIntroScreen() {
        val intent = intentFactory.startIntroActivity(activity)
        activity.startActivity(intent)
    }

    override fun showMessage(m: ListHabitsBehavior.Message) {
        activity.showMessage(
            activity.resources.getString(
                when (m) {
                    COULD_NOT_EXPORT -> R.string.could_not_export
                    IMPORT_SUCCESSFUL -> R.string.habits_imported
                    IMPORT_FAILED -> R.string.could_not_import
                    DATABASE_REPAIRED -> R.string.database_repaired
                    COULD_NOT_GENERATE_BUG_REPORT -> R.string.bug_report_failed
                    FILE_NOT_RECOGNIZED -> R.string.file_not_recognized
                }
            )
        )
    }

    override fun showSendBugReportToDeveloperScreen(log: String) {
        val to = R.string.bugReportTo
        val subject = R.string.bugReportSubject
        activity.showSendEmailScreen(to, subject, log)
    }

    override fun showSendFileScreen(filename: String) {
        activity.showSendFileScreen(filename)
    }

    override fun showConfetti(color: PaletteColor, x: Float, y: Float) {
        if (x == 0f && y == 0f) return
        if (preferences.isConfettiAnimationDisabled) return
        if (Settings.Global.getFloat(
                activity.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1f
            ) == 0f
        ) {
            return
        }
        val baseColor = themeSwitcher.currentTheme!!.color(color).toInt()
        rootView.get().konfettiView.start(
            Party(
                speed = 0f,
                maxSpeed = 16f,
                damping = 0.9f,
                spread = 360,
                angle = 0,
                colors = listOf(
                    ColorUtils.changeHue(baseColor, 180f),
                    ColorUtils.changeHue(baseColor, 20f),
                    ColorUtils.changeHue(baseColor, -20f),
                    baseColor
                ),
                position = Position.Absolute(x, y),
                emitter = Emitter(duration = 25, TimeUnit.MILLISECONDS).max(25),
                timeToLive = 0
            )
        )
    }

    override fun showSettingsScreen() {
        val intent = intentFactory.startSettingsActivity(activity)
        activity.startActivityForResult(intent, REQUEST_SETTINGS)
    }

    override fun showColorPicker(defaultColor: PaletteColor, callback: OnColorPickedCallback) {
        val picker = colorPickerFactory.create(defaultColor, themeSwitcher.currentTheme!!)
        picker.setListener(callback)
        picker.dismissCurrentAndShow(activity.supportFragmentManager, "picker")
    }

    override fun showNumberPopup(
        value: Double,
        notes: String,
        callback: ListHabitsBehavior.NumberPickerCallback
    ) {
        val fm = (context as AppCompatActivity).supportFragmentManager
        val dialog = NumberDialog()
        dialog.arguments = Bundle().apply {
            putDouble("value", value)
            putString("notes", notes)
        }
        dialog.onToggle = { v, n -> callback.onNumberPicked(v, n) }
        dialog.dismissCurrentAndShow(fm, "numberDialog")
    }

    override fun showCheckmarkPopup(
        selectedValue: Int,
        notes: String,
        color: PaletteColor,
        callback: ListHabitsBehavior.CheckMarkDialogCallback
    ) {
        val theme = rootView.get().currentTheme()
        val fm = (context as AppCompatActivity).supportFragmentManager
        val dialog = CheckmarkDialog()
        dialog.arguments = Bundle().apply {
            putInt("color", theme.color(color).toInt())
            putInt("value", selectedValue)
            putString("notes", notes)
        }
        dialog.onToggle = { v, n -> callback.onNotesSaved(v, n) }
        dialog.dismissCurrentAndShow(fm, "checkmarkDialog")
    }

    private fun getExecuteString(command: Command): String? {
        when (command) {
            is ArchiveHabitsCommand -> {
                return activity.resources.getQuantityString(
                    R.plurals.toast_habits_archived,
                    command.selected.size
                )
            }

            is ChangeHabitColorCommand -> {
                return activity.resources.getQuantityString(
                    R.plurals.toast_habits_changed,
                    command.selected.size
                )
            }

            is CreateHabitCommand -> {
                return activity.resources.getString(R.string.toast_habit_created)
            }

            is DeleteHabitsCommand -> {
                return activity.resources.getQuantityString(
                    R.plurals.toast_habits_deleted,
                    command.selected.size
                )
            }

            is EditHabitCommand -> {
                return activity.resources.getQuantityString(R.plurals.toast_habits_changed, 1)
            }

            is UnarchiveHabitsCommand -> {
                return activity.resources.getQuantityString(
                    R.plurals.toast_habits_unarchived,
                    command.selected.size
                )
            }

            else -> return null
        }
    }

    private fun onImportData(file: File, onFinished: () -> Unit) {
        taskRunner.execute(
            importTaskFactory.create(file) { result ->
                when (result) {
                    ImportDataTask.SUCCESS -> {
                        adapter.refresh()
                        activity.showMessage(activity.resources.getString(R.string.habits_imported))
                    }

                    ImportDataTask.NOT_RECOGNIZED -> {
                        activity.showMessage(activity.resources.getString(R.string.file_not_recognized))
                    }

                    else -> {
                        activity.showMessage(activity.resources.getString(R.string.could_not_import))
                    }
                }
                onFinished()
            }
        )
    }

    private fun onExportDB() {
        taskRunner.execute(
            exportDBFactory.create { filename ->
                if (filename != null) {
                    activity.showSendFileScreen(filename)
                } else {
                    activity.showMessage(activity.resources.getString(R.string.could_not_export))
                }
            }
        )
    }
}
