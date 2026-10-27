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
