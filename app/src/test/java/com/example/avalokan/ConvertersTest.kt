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

package com.example.avalokan

import com.example.avalokan.data.Converters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun `empty list round-trips as an empty column`() {
        assertEquals("", converters.fromStringList(emptyList()))
        assertTrue(converters.toStringList("").isEmpty())
        assertTrue(converters.toStringList(null).isEmpty())
    }

    @Test
    fun `list of ids round-trips without loss`() {
        val ids = listOf("boudhanath", "kathmandu-durbar", "patan-durbar")

        assertEquals(ids, converters.toStringList(converters.fromStringList(ids)))
    }

    @Test
    fun `ids containing separators round-trip safely`() {
        val ids = listOf("place,with,commas", "percent%sign", "plain")

        assertEquals(ids, converters.toStringList(converters.fromStringList(ids)))
    }

    @Test
    fun `null list is written as an empty column`() {
        assertEquals("", converters.fromStringList(null))
    }
}
