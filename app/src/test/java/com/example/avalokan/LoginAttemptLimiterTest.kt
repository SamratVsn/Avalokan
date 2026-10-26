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
