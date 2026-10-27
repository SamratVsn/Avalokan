package com.example.avalokan.data.security

/**
 * Shared input checks for auth and persisted identifiers.
 *
 * Room uses bound parameters (`:id`), so SQL injection is not possible when queries
 * stay static; these rules still block malformed navigation/deep-link IDs before
 * they reach Room or Firestore document paths.
 */

private val EMAIL_PATTERN =
    Regex("""^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$""")

/** Firestore / app slug IDs (e.g. `kathmandu-durbar`). */
private val DOCUMENT_ID_PATTERN =
    Regex("""^[a-zA-Z0-9][a-zA-Z0-9_-]{0,127}$""")

const val SIGN_IN_MIN_PASSWORD_LENGTH = 6
const val SIGN_UP_MIN_PASSWORD_LENGTH = 8

fun isValidEmail(email: String): Boolean =
    email.trim().length <= 320 && EMAIL_PATTERN.matches(email.trim())

fun isSafeDocumentId(id: String): Boolean =
    id.isNotBlank() && DOCUMENT_ID_PATTERN.matches(id)

/**
 * Passwords are hashed by Firebase Auth on the server (scrypt). The app never
 * stores or pre-hashes them — only validates strength before sign-up.
 */
fun signUpPasswordError(password: CharSequence): String? {
    if (password.length < SIGN_UP_MIN_PASSWORD_LENGTH) {
        return "Use at least $SIGN_UP_MIN_PASSWORD_LENGTH characters for your password."
    }
    if (!password.any { it.isLetter() }) {
        return "Include at least one letter in your password."
    }
    if (!password.any { it.isDigit() }) {
        return "Include at least one number in your password."
    }
    return null
}

fun canSubmitSignIn(email: String, password: String): Boolean =
    isValidEmail(email) && password.length >= SIGN_IN_MIN_PASSWORD_LENGTH

fun canSubmitSignUp(email: String, password: String): Boolean =
    isValidEmail(email) && signUpPasswordError(password) == null
