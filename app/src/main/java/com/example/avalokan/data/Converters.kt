/*
 * Copyright (C) 2026 Samrat Parajuli
 *
 * This file is part of Avalokan.
 *
 * Avalokan is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * Avalokan is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Avalokan. If not, see <https://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */

package com.example.avalokan.data

import androidx.room3.ColumnTypeConverter

class Converters {

    @ColumnTypeConverter
    fun fromStringList(values: List<String>?): String =
        values.orEmpty().joinToString(SEPARATOR.toString()) { escape(it) }

    @ColumnTypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        return value.split(SEPARATOR).map { unescape(it) }
    }

    private fun escape(value: String): String = buildString(value.length) {
        value.forEach { char ->
            when (char) {
                ESCAPE -> append("%25")
                SEPARATOR -> append("%2C")
                else -> append(char)
            }
        }
    }

    private fun unescape(value: String): String = buildString(value.length) {
        var index = 0
        while (index < value.length) {
            val char = value[index]
            if (char == ESCAPE && index + 2 < value.length) {
                when (value.substring(index + 1, index + 3)) {
                    "25" -> {
                        append(ESCAPE)
                        index += 3
                        continue
                    }
                    "2C" -> {
                        append(SEPARATOR)
                        index += 3
                        continue
                    }
                }
            }
            append(char)
            index++
        }
    }

    private companion object {
        const val SEPARATOR = ','
        const val ESCAPE = '%'
    }
}
