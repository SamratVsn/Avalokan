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

import com.example.avalokan.data.security.canSubmitSignIn
import com.example.avalokan.data.security.canSubmitSignUp
import com.example.avalokan.data.security.isSafeDocumentId
import com.example.avalokan.data.security.isValidEmail
import com.example.avalokan.data.security.signUpPasswordError
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InputValidationTest {

    @Test
    fun `document ids reject injection-like strings`() {
        assertTrue(isSafeDocumentId("kathmandu-durbar"))
        assertFalse(isSafeDocumentId("'; DROP TABLE places;--"))
        assertFalse(isSafeDocumentId("../admin"))
        assertFalse(isSafeDocumentId(""))
    }

    @Test
    fun `email validation rejects obvious garbage`() {
        assertTrue(isValidEmail("user@example.com"))
        assertFalse(isValidEmail("not-an-email"))
        assertFalse(isValidEmail("@missing.local"))
    }

    @Test
    fun `sign-up password requires length letter and digit`() {
        assertNotNull(signUpPasswordError("short1"))
        assertNotNull(signUpPasswordError("allletters"))
        assertNotNull(signUpPasswordError("12345678"))
        assertNull(signUpPasswordError("Heritage8"))
    }

    @Test
    fun `submit gates match sign-in and sign-up rules`() {
        assertTrue(canSubmitSignIn("a@b.co", "123456"))
        assertFalse(canSubmitSignIn("bad", "123456"))
        assertTrue(canSubmitSignUp("a@b.co", "Nepal2026"))
        assertFalse(canSubmitSignUp("a@b.co", "weak"))
    }
}
