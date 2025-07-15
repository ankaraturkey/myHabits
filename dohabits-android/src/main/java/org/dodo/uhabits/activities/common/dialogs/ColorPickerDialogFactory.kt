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

import android.content.Context
import com.android.colorpicker.ColorPickerDialog.SIZE_SMALL
import org.dodo.platform.gui.toInt
import com.dodo.dohabits.R
import com.dodo.dohabits.core.models.PaletteColor
import com.dodo.dohabits.core.ui.views.Theme
import com.dodo.dohabits.inject.ActivityContext
import com.dodo.dohabits.inject.ActivityScope
import com.dodo.dohabits.utils.StyledResources
import javax.inject.Inject

@ActivityScope
class ColorPickerDialogFactory @Inject constructor(@param:ActivityContext private val context: Context) {
    fun create(color: PaletteColor, theme: Theme): ColorPickerDialog {
        val dialog = ColorPickerDialog()
        val res = StyledResources(context)
        val androidColor = theme.color(color).toInt()
        dialog.initialize(
            R.string.color_picker_default_title,
            res.getPalette(),
            androidColor,
            4,
            SIZE_SMALL
        )
        return dialog
    }
}
