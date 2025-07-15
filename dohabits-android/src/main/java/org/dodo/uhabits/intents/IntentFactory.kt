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

package com.dodo.dohabits.intents

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.dodo.dohabits.R
import com.dodo.dohabits.activities.habits.edit.EditHabitActivity
import com.dodo.dohabits.activities.habits.show.ShowHabitActivity
import com.dodo.dohabits.activities.intro.IntroActivity
import com.dodo.dohabits.activities.settings.SettingsActivity
import com.dodo.dohabits.core.models.Habit
import javax.inject.Inject

class IntentFactory
@Inject constructor() {

    fun helpTranslate(context: Context) =
        buildViewIntent(context.getString(R.string.translateURL))

    fun openDocument() = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "*/*"
    }

    fun sendFeedback(context: Context) =
        buildSendToIntent(context.getString(R.string.feedbackURL))

    fun privacyPolicy(context: Context) =
        buildViewIntent(context.getString(R.string.privacyPolicyURL))

    fun startIntroActivity(context: Context) =
        Intent(context, IntroActivity::class.java)

    fun startSettingsActivity(context: Context) =
        Intent(context, SettingsActivity::class.java)

    fun startShowHabitActivity(context: Context, habit: Habit) =
        Intent(context, ShowHabitActivity::class.java).apply {
            data = Uri.parse(habit.uriString)
        }

    fun viewFAQ(context: Context) =
        buildViewIntent(context.getString(R.string.helpURL))

    fun viewSourceCode(context: Context) =
        buildViewIntent(context.getString(R.string.sourceCodeURL))

    private fun buildSendToIntent(url: String) = Intent().apply {
        action = Intent.ACTION_SENDTO
        data = Uri.parse(url)
    }

    private fun buildViewIntent(url: String) = Intent().apply {
        action = Intent.ACTION_VIEW
        data = Uri.parse(url)
    }

    fun codeContributors(context: Context) =
        buildViewIntent(context.getString(R.string.codeContributorsURL))

    private fun startEditActivity(context: Context): Intent {
        return Intent(context, EditHabitActivity::class.java)
    }

    fun startEditActivity(context: Context, habit: Habit): Intent {
        val intent = startEditActivity(context)
        intent.putExtra("habitId", habit.id)
        intent.putExtra("habitType", habit.type)
        return intent
    }

    fun startEditActivity(context: Context, habitType: Int): Intent {
        val intent = startEditActivity(context)
        intent.putExtra("habitType", habitType)
        return intent
    }
}
