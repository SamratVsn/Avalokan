package com.example.avalokan.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
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

private fun friendlyMessage(e: Exception): String =
    e.localizedMessage?.takeIf { it.isNotBlank() } ?: "Authentication failed. Try again."

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
        }.getOrElse { AuthResult.Error(friendlyMessage(it as Exception)) }

    suspend fun signUpEmail(email: String, password: String): AuthResult =
        runCatching {
            suspendCancellableCoroutine { cont ->
                auth.createUserWithEmailAndPassword(email.trim(), password)
                    .addOnSuccessListener { cont.resume(Unit) {} }
                    .addOnFailureListener { cont.resumeWithException(it) }
            }
            AuthResult.Success
        }.getOrElse { AuthResult.Error(friendlyMessage(it as Exception)) }

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
        }.getOrElse { AuthResult.Error(friendlyMessage(it as Exception)) }

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
