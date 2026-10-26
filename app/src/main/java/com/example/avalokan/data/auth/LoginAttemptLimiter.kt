package com.example.avalokan.data.auth

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

private const val PREFS_NAME = "login_attempts"
private const val MAX_ATTEMPTS = 5
private const val LOCKOUT_MS = 15 * 60 * 1000L

private val Context.loginAttemptStore by preferencesDataStore(
    name = PREFS_NAME,
    corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
)

/**
 * Tracks failed email/password sign-in attempts per account identifier (SHA-256
 * of normalized email). Passwords are never stored — Firebase Auth hashes them
 * on the server; this only rate-limits brute-force attempts on the device.
 */
@Singleton
class LoginAttemptLimiter @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun lockoutMessage(): String =
        "Too many failed sign-in attempts. Please wait 15 minutes and try again."

    suspend fun blockReasonIfLocked(email: String): String? {
        val (count, lockedUntil) = readState(emailKey(email))
        val now = System.currentTimeMillis()
        if (lockedUntil > now) return lockoutMessage()
        if (lockedUntil > 0L && lockedUntil <= now) {
            clear(email)
        } else if (count >= MAX_ATTEMPTS) {
            return lockoutMessage()
        }
        return null
    }

    suspend fun recordFailedAttempt(email: String) {
        val key = emailKey(email)
        val (count, lockedUntil) = readState(key)
        val now = System.currentTimeMillis()
        if (lockedUntil > now) return

        val next = count + 1
        val lock = if (next >= MAX_ATTEMPTS) now + LOCKOUT_MS else 0L
        writeState(key, next, lock)
    }

    suspend fun clearAttempts(email: String) {
        clear(emailKey(email))
    }

    internal fun hashEmailForAttempts(email: String): String = emailKey(email)

    private fun emailKey(email: String): String {
        val normalized = email.trim().lowercase()
        val digest = MessageDigest.getInstance("SHA-256").digest(normalized.toByteArray())
        return digest.joinToString("") { byte -> "%02x".format(byte) }
    }

    private fun countKey(id: String) = intPreferencesKey("fail_count_$id")
    private fun lockKey(id: String) = longPreferencesKey("lock_until_$id")

    private suspend fun readState(id: String): Pair<Int, Long> {
        val prefs = context.loginAttemptStore.data.first()
        val count = prefs[countKey(id)] ?: 0
        val lockedUntil = prefs[lockKey(id)] ?: 0L
        return count to lockedUntil
    }

    private suspend fun writeState(id: String, count: Int, lockedUntil: Long) {
        context.loginAttemptStore.edit { prefs ->
            prefs[countKey(id)] = count
            if (lockedUntil > 0L) {
                prefs[lockKey(id)] = lockedUntil
            } else {
                prefs.remove(lockKey(id))
            }
        }
    }

    private suspend fun clear(id: String) {
        context.loginAttemptStore.edit { prefs ->
            prefs.remove(countKey(id))
            prefs.remove(lockKey(id))
        }
    }
}

internal fun isCredentialFailure(e: Throwable): Boolean {
    val code = (e as? com.google.firebase.auth.FirebaseAuthException)?.errorCode ?: return false
    return code in CREDENTIAL_FAILURE_CODES
}

private val CREDENTIAL_FAILURE_CODES = setOf(
    "ERROR_WRONG_PASSWORD",
    "ERROR_INVALID_CREDENTIAL",
    "ERROR_USER_NOT_FOUND",
    "ERROR_INVALID_EMAIL",
)
