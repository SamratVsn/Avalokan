package com.example.avalokan.data.firestore

import android.util.Log
import com.example.avalokan.data.security.isSafeDocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

/** Console fields may be stored as String or Number — coerce either. */
internal fun DocumentSnapshot.textField(name: String): String =
    get(name)?.toString().orEmpty()

/**
 * Shared, cache-first Firestore plumbing for a single collection.
 *
 * Repositories only supply the collection name, the Room-backed cache
 * operations and a document mapper. The listener lifecycle, cache write-through,
 * server refresh and error handling therefore live in one place instead of three
 * copies that can drift apart.
 */
internal class FirestoreCachedCollection<T>(
    private val firestore: FirebaseFirestore,
    private val collectionName: String,
    private val observeCached: () -> Flow<List<T>>,
    private val observeCachedById: (String) -> Flow<T?>,
    private val upsert: suspend (List<T>) -> Unit,
    private val clear: suspend () -> Unit,
    private val mapper: (DocumentSnapshot) -> T,
) {
    /**
     * Emits the Room cache immediately, then every live Firestore snapshot.
     * A failed listener keeps the last good emission instead of blanking the UI.
     */
    fun observeAll(): Flow<List<T>> = callbackFlow {
        send(observeCached().first())
        val registration = firestore.collection(collectionName)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Firestore listen failed for $collectionName", error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents.orEmpty().map { mapper(it) }
                launch { upsert(items) }
                trySend(items)
            }
        awaitClose { registration.remove() }
    }

    /**
     * Single-document variant used by detail screens. A document that does not
     * exist resolves to null so the UI can show its not-found state instead of
     * rendering a blank entity.
     */
    fun observeById(id: String): Flow<T?> {
        if (!isSafeDocumentId(id)) return flowOf(null)
        return callbackFlow {
            send(observeCachedById(id).first())
            val registration = firestore.collection(collectionName).document(id)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Document listen failed for $collectionName/$id", error)
                        return@addSnapshotListener
                    }
                    val item = snapshot?.takeIf { it.exists() }?.let { mapper(it) }
                    if (item != null) {
                        launch { upsert(listOf(item)) }
                        trySend(item)
                    } else {
                        trySend(null)
                    }
                }
            awaitClose { registration.remove() }
        }
    }

    /** Forces a server read, bypassing Firestore's own cache, then caches it. */
    suspend fun refresh() {
        val items = suspendCancellableCoroutine { cont ->
            firestore.collection(collectionName).get(Source.SERVER)
                .addOnSuccessListener { snapshot ->
                    cont.resume(snapshot.documents.map { mapper(it) }) {}
                }
                .addOnFailureListener { e -> cont.resumeWithException(e) }
        }
        upsert(items)
    }

    suspend fun clearCache() = clear()

    private companion object {
        const val TAG = "AvalokanRepo"
    }
}
