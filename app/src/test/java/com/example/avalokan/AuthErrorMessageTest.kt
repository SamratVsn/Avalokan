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

import com.example.avalokan.data.auth.authErrorMessageFor
import com.example.avalokan.data.auth.friendlyMessage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.net.UnknownHostException

/**
 * The auth screens show these strings verbatim, so the mapping from a
 * low-level failure to user-facing copy is worth pinning down.
 */
class AuthErrorMessageTest {

    @Test
    fun `every known firebase code maps to specific guidance`() {
        assertEquals(
            "That email address doesn’t look right. Please check it and try again.",
            authErrorMessageFor("ERROR_INVALID_EMAIL")
        )
        assertEquals(
            "Incorrect email or password. Please double-check and try again.",
            authErrorMessageFor("ERROR_WRONG_PASSWORD")
        )
        assertEquals(
            "Incorrect email or password. Please double-check and try again.",
            authErrorMessageFor("ERROR_INVALID_CREDENTIAL")
        )
        assertEquals(
            "We couldn’t find an account with that email. Try creating one instead.",
            authErrorMessageFor("ERROR_USER_NOT_FOUND")
        )
        assertEquals(
            "That email is already registered. Try logging in instead.",
            authErrorMessageFor("ERROR_EMAIL_ALREADY_IN_USE")
        )
        assertEquals(
            "Too many attempts. Please wait a few minutes and try again.",
            authErrorMessageFor("ERROR_TOO_MANY_REQUESTS")
        )
    }

    @Test
    fun `unknown and missing codes fall through to the caller`() {
        assertNull(authErrorMessageFor(null))
        assertNull(authErrorMessageFor(""))
        assertNull(authErrorMessageFor("ERROR_SOMETHING_NEW"))
    }

    @Test
    fun `network failures explain the connection problem`() {
        val expected = "No internet connection. Check your network and try again."
        assertEquals(expected, friendlyMessage(IOException()))
        assertEquals(expected, friendlyMessage(UnknownHostException("avalokan.example")))
    }

    @Test
    fun `unrecognised failures fall back to a neutral apology`() {
        assertEquals(
            "Something went wrong while signing you in. Please try again.",
            friendlyMessage(IllegalStateException("boom"))
        )
    }

    @Test
    fun `messages never leak raw exception details`() {
        val message = friendlyMessage(RuntimeException("credential=abc123 secret=xyz"))
        assertFalse(message.contains("abc123"))
        assertFalse(message.contains("xyz"))
        assertTrue(message.length < 100)
    }
}
