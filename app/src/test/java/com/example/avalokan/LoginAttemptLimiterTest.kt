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

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.security.MessageDigest

class LoginAttemptLimiterTest {

    @Test
    fun emailHash_isStableAndDoesNotContainPlainEmail() {
        val email = "User@Example.com"
        val hash = hashEmail(normalizeEmail(email))
        assertFalse(hash.contains("user"))
        assertFalse(hash.contains("example"))
        assertTrue(hash.length == 64)
        assertTrue(hash == hashEmail(normalizeEmail("user@example.com")))
    }

    private fun normalizeEmail(email: String) = email.trim().lowercase()

    private fun hashEmail(normalized: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(normalized.toByteArray())
        return digest.joinToString("") { byte -> "%02x".format(byte) }
    }
}
