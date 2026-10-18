package com.example.avalokan.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resumeWithException

/** Result surface for auth operations; errors carry user-readable messages. */
sealed interface AuthResult {
    data object Success : AuthResult
    data class Error(val message: String) : AuthResult
}

/**
 * Turns low-level Firebase / Google exceptions into short, human, actionable
 * messages. Anything unrecognised falls back to a neutral apology so the UI
 * never shows a raw stack-trace style string.
 */
private fun friendlyMessage(e: Throwable): String {
    if (e is GetCredentialCancellationException) {
        return "Google sign-in was cancelled. You can try again anytime."
    }
    return when ((e as? FirebaseAuthException)?.errorCode) {
        "ERROR_INVALID_EMAIL" ->
            "That email address doesn’t look right. Please check it and try again."
        "ERROR_WRONG_PASSWORD", "ERROR_INVALID_CREDENTIAL" ->
            "Incorrect email or password. Please double-check and try again."
        "ERROR_USER_NOT_FOUND" ->
            "We couldn’t find an account with that email. Try creating one instead."
        "ERROR_USER_DISABLED" ->
            "This account has been disabled. Please contact support for help."
        "ERROR_EMAIL_ALREADY_IN_USE", "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" ->
            "That email is already registered. Try logging in instead."
        "ERROR_WEAK_PASSWORD" ->
            "Your password is too weak. Use at least 6 characters."
        "ERROR_NETWORK_REQUEST_FAILED" ->
            "No internet connection. Check your network and try again."
        "ERROR_TOO_MANY_REQUESTS" ->
            "Too many attempts. Please wait a few minutes and try again."
        "ERROR_OPERATION_NOT_ALLOWED" ->
            "This sign-in method isn’t available right now. Please try another."
        else -> when (e) {
            is java.net.UnknownHostException, is java.io.IOException ->
                "No internet connection. Check your network and try again."
            else -> "Something went wrong while signing you in. Please try again."
        }
    }
}

/**
 * Email + Google auth over Firebase. Anonymous installs upgrade via account
 * linking, so existing users/{uid} data follows the new login.
 */
@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _currentUser = MutableStateFlow(auth.currentUser)
    val currentUser: Flow<FirebaseUser?> = _currentUser.asStateFlow()

    private val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _currentUser.value = firebaseAuth.currentUser
    }

    init {
        auth.addAuthStateListener(listener)
    }

    val isSignedInWithAccount: Boolean
        get() = auth.currentUser?.let { !it.isAnonymous } ?: false

    suspend fun signInEmail(email: String, password: String): AuthResult =
        runCatching {
            suspendCancellableCoroutine { cont ->
                auth.signInWithEmailAndPassword(email.trim(), password)
                    .addOnSuccessListener { cont.resume(Unit) {} }
                    .addOnFailureListener { cont.resumeWithException(it) }
            }
            AuthResult.Success
        }.getOrElse { AuthResult.Error(friendlyMessage(it)) }

    suspend fun signUpEmail(email: String, password: String): AuthResult =
        runCatching {
            suspendCancellableCoroutine { cont ->
                auth.createUserWithEmailAndPassword(email.trim(), password)
                    .addOnSuccessListener { cont.resume(Unit) {} }
                    .addOnFailureListener { cont.resumeWithException(it) }
            }
            AuthResult.Success
        }.getOrElse { AuthResult.Error(friendlyMessage(it)) }

    suspend fun signInGoogle(serverClientId: String): AuthResult =
        runCatching {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(serverClientId)
                .setAutoSelectEnabled(false)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            val result = CredentialManager.create(context).getCredential(context, request)
            val googleCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
            val firebaseCredential =
                GoogleAuthProvider.getCredential(googleCredential.idToken, null)
            linkOrSignIn(firebaseCredential)
            AuthResult.Success
        }.getOrElse { AuthResult.Error(friendlyMessage(it)) }

    private suspend fun linkOrSignIn(credential: AuthCredential) {
        val user = auth.currentUser
        if (user != null && user.isAnonymous) {
            suspendCancellableCoroutine { cont ->
                user.linkWithCredential(credential)
                    .addOnSuccessListener { cont.resume(Unit) {} }
                    .addOnFailureListener { cont.resumeWithException(it) }
            }
        } else {
            suspendCancellableCoroutine { cont ->
                auth.signInWithCredential(credential)
                    .addOnSuccessListener { cont.resume(Unit) {} }
                    .addOnFailureListener { cont.resumeWithException(it) }
            }
        }
    }

    fun signOut() {
        auth.signOut()
        // Re-enter anonymously so Firestore reads keep working.
        auth.signInAnonymously()
            .addOnFailureListener { e ->
                Log.w("AvalokanRepo", "Anonymous re-sign-in failed", e)
            }
    }
}
