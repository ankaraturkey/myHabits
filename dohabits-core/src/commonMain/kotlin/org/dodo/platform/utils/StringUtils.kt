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

package org.dodo.platform.utils

class StringUtils {

    companion object {

        fun joinLongs(values: LongArray): String = values.joinToString(separator = ",")

        fun splitLongs(str: String): LongArray {
            return try {
                str.split(",").map { it.toLong() }.toLongArray()
            } catch (e: NumberFormatException) {
                LongArray(0)
            }
        }
    }
}
