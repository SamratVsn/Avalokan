package com.example.avalokan.data.user

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

interface UserRepository {
    fun observeUser(uid: String): Flow<UserProfile?>
    suspend fun saveUser(profile: UserProfile)
}

class FirestoreUserRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {
    override fun observeUser(uid: String): Flow<UserProfile?> = callbackFlow {
        val registration = firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Keep the last good emission rather than dropping the profile.
                    Log.w("AvalokanRepo", "User listen failed", error)
                    return@addSnapshotListener
                }
                val profile = snapshot?.takeIf { it.exists() }?.let { doc ->
                    UserProfile(
                        uid = doc.id,
                        displayName = doc.getString("displayName").orEmpty().ifBlank { "Guest" },
                        bio = doc.getString("bio").orEmpty(),
                        avatarUrl = doc.getString("avatarUrl"),
                        updatedAt = doc.getLong("updatedAt") ?: 0L
                    )
                }
                trySend(profile)
            }
        awaitClose { registration.remove() }
    }

    override suspend fun saveUser(profile: UserProfile) {
        val data = mapOf(
            "displayName" to profile.displayName,
            "bio" to profile.bio,
            "avatarUrl" to profile.avatarUrl,
            "updatedAt" to System.currentTimeMillis()
        )
        suspendCancellableCoroutine { cont ->
            firestore.collection("users").document(profile.uid)
                .set(data, SetOptions.merge())
                .addOnSuccessListener { cont.resume(Unit) {} }
                .addOnFailureListener { cont.resumeWithException(it) }
        }
    }
}