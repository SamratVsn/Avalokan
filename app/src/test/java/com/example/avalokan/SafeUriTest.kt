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

import com.example.avalokan.data.security.safeHttpUrl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SafeUriTest {

    @Test
    fun `allows http and https and normalizes bare hosts`() {
        assertEquals("https://example.com/path", safeHttpUrl("example.com/path"))
        assertEquals("https://example.com", safeHttpUrl("https://example.com"))
    }

    @Test
    fun `blocks dangerous schemes`() {
        assertNull(safeHttpUrl("javascript:alert(1)"))
        assertNull(safeHttpUrl("file:///etc/passwd"))
        assertNull(safeHttpUrl("intent://evil#Intent;scheme=https;end"))
    }

    @Test
    fun `blank input is rejected`() {
        assertNull(safeHttpUrl("   "))
    }
}
