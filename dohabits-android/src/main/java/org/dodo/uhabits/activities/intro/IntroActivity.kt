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

package com.dodo.dohabits.activities.intro

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroFragment
import com.dodo.dohabits.R

/**
 * Activity that introduces the app to the user, shown only after the app is
 * launched for the first time.
 */
class IntroActivity : AppIntro2() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showStatusBar(false)

        addSlide(
            AppIntroFragment.newInstance(
                getString(R.string.intro_title_1),
                getString(R.string.intro_description_1),
                R.drawable.intro_icon_1,
                Color.parseColor("#F0F0F0"), // 背景色
                Color.parseColor("#512DA8"), // 标题颜色（紫色）
                Color.parseColor("#512DA8")  // 描述颜色（紫色）
            )
        )

        addSlide(
            AppIntroFragment.newInstance(
                getString(R.string.intro_title_2),
                getString(R.string.intro_description_2),
                R.drawable.intro_icon_2,
                Color.parseColor("#90CAF9")
            )
        )

        addSlide(
            AppIntroFragment.newInstance(
                getString(R.string.intro_title_4),
                getString(R.string.intro_description_4),
                R.drawable.intro_icon_4,
                Color.parseColor("#81C784")
            )
        )
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        finish()
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        finish()
    }
}
