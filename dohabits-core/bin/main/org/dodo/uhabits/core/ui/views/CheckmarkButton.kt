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

package com.dodo.dohabits.core.ui.views

import org.dodo.platform.gui.Canvas
import org.dodo.platform.gui.Color
import org.dodo.platform.gui.Font
import org.dodo.platform.gui.FontAwesome
import org.dodo.platform.gui.View

class CheckmarkButton(
    private val value: Int,
    private val color: Color,
    private val theme: Theme
) : View {
    override fun draw(canvas: Canvas) {
        canvas.setFont(Font.FONT_AWESOME)
        canvas.setFontSize(theme.smallTextSize * 1.5)
        canvas.setColor(
            when (value) {
                2 -> color
                else -> theme.lowContrastTextColor
            }
        )
        val text = when (value) {
            0 -> FontAwesome.TIMES
            else -> FontAwesome.CHECK
        }
        canvas.drawText(text, canvas.getWidth() / 2.0, canvas.getHeight() / 2.0)
    }
}
