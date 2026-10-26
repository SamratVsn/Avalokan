package com.example.avalokan.data.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

data class AuthState(
    val isAuthenticated: Boolean = false,
    val isAnonymous: Boolean = true,
    val uid: String? = null,
) {
    /**
     * Real, named account only (not anonymous/guest). Used to gate features
     * that must belong to a durable account (saving places, joining events).
     */
}

fun FirebaseAuth.observeAuthState(): Flow<AuthState> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser
        trySend(
            AuthState(
                isAuthenticated = user != null,
                isAnonymous = user?.isAnonymous == true,
                uid = user?.uid,
            )
        )
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}.distinctUntilChanged()
