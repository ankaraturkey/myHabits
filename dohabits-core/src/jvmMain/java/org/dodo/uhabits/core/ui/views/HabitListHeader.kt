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
import org.dodo.platform.gui.Font
import org.dodo.platform.gui.View
import org.dodo.platform.time.LocalDate
import org.dodo.platform.time.LocalDateFormatter

class HabitListHeader(
    private val today: LocalDate,
    private val nButtons: Int,
    private val theme: Theme,
    private val fmt: LocalDateFormatter
) : View {

    override fun draw(canvas: Canvas) {
        val width = canvas.getWidth()
        val height = canvas.getHeight()
        val buttonSize = theme.checkmarkButtonSize
        canvas.setColor(theme.headerBackgroundColor)
        canvas.fillRect(0.0, 0.0, width, height)

        canvas.setColor(theme.headerBorderColor)
        canvas.setStrokeWidth(0.5)
        canvas.drawLine(0.0, height - 0.5, width, height - 0.5)

        canvas.setColor(theme.headerTextColor)
        canvas.setFont(Font.BOLD)
        canvas.setFontSize(theme.smallTextSize)

        repeat(nButtons) { index ->
            val date = today.minus(nButtons - index - 1)
            val name = fmt.shortWeekdayName(date).uppercase()
            val number = date.day.toString()

            val x = width - (index + 1) * buttonSize + buttonSize / 2
            val y = height / 2
            canvas.drawText(name, x, y - theme.smallTextSize * 0.6)
            canvas.drawText(number, x, y + theme.smallTextSize * 0.6)
        }
    }
}
