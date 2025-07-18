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

package com.dodo.dohabits.activities

import android.app.Activity
import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build.VERSION.SDK_INT
import androidx.core.content.ContextCompat
import com.dodo.dohabits.R
import com.dodo.dohabits.core.preferences.Preferences
import com.dodo.dohabits.core.ui.ThemeSwitcher
import com.dodo.dohabits.core.ui.views.DarkTheme
import com.dodo.dohabits.core.ui.views.LightTheme
import com.dodo.dohabits.core.ui.views.PureBlackTheme
import com.dodo.dohabits.core.ui.views.Theme
import com.dodo.dohabits.inject.ActivityContext
import com.dodo.dohabits.inject.ActivityScope

@ActivityScope
class AndroidThemeSwitcher
constructor(
    @ActivityContext val context: Context,
    preferences: Preferences
) : ThemeSwitcher(preferences) {

    override var currentTheme: Theme = LightTheme()

    override fun getSystemTheme(): Int {
        if (SDK_INT < 29) return THEME_LIGHT
        val uiMode = context.resources.configuration.uiMode
        return if ((uiMode and UI_MODE_NIGHT_MASK) == UI_MODE_NIGHT_YES) {
            THEME_DARK
        } else {
            THEME_LIGHT
        }
    }

    override fun applyDarkTheme() {
        currentTheme = DarkTheme()
        context.setTheme(R.style.AppBaseThemeDark)
        (context as Activity).window.navigationBarColor =
            ContextCompat.getColor(context, R.color.white)
    }

    override fun applyLightTheme() {
        currentTheme = LightTheme()
        context.setTheme(R.style.AppBaseTheme)
        (context as Activity).window.navigationBarColor =
            ContextCompat.getColor(context, R.color.white)
    }

    override fun applyPureBlackTheme() {
        currentTheme = PureBlackTheme()
        context.setTheme(R.style.AppBaseThemeDark_PureBlack)
        (context as Activity).window.navigationBarColor =
            ContextCompat.getColor(context, R.color.black)
    }
}
