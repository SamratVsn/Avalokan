package com.example.avalokan.data.firestore

import android.util.Log
import com.example.avalokan.data.security.isSafeDocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

/** Console fields may be stored as String or Number — coerce either. */
internal fun DocumentSnapshot.textField(name: String): String =
    get(name)?.toString().orEmpty()

/**
 * Shared, Room-first Firestore plumbing for a single collection.
 *
 * Room is the single source of truth. The Firestore listener never emits to
 * the UI directly — it only writes snapshots into Room, and every observer
 * reads the same Room flow. That removes the old two-stream race (one-shot
 * Room read followed by a separate Firestore stream), makes offline reads
 * deterministic, and gives us one place to record sync metadata such as
 * "last updated".
 *
 * Repositories only supply the collection name, the Room-backed cache
 * operations and a document mapper.
 */
internal class FirestoreCachedCollection<T>(
    private val firestore: FirebaseFirestore,
    private val collectionName: String,
    private val observeCached: () -> Flow<List<T>>,
    private val observeCachedById: (String) -> Flow<T?>,
    private val replaceAll: suspend (List<T>) -> Unit,
    private val upsert: suspend (List<T>) -> Unit,
    private val deleteById: suspend (String) -> Unit,
    private val clear: suspend () -> Unit,
    private val mapper: (DocumentSnapshot) -> T,
) {
    /**
     * Emits Room's cache immediately and keeps emitting as Firestore snapshots
     * are written through to Room. A failed listener leaves the last good
     * Room emission in place instead of blanking the UI.
     */
    fun observeAll(): Flow<List<T>> = flow {
        coroutineScope {
            val listener = launch {
                collectionSnapshots()
                    .catch { e -> Log.w(TAG, "Firestore listen failed for $collectionName", e) }
                    .collect { snapshot ->
                        try {
                            val items = snapshot.documents.map { mapper(it) }
                            // Firestore's local cache can be empty while Room still has
                            // a good copy (for example, a cold offline start). Wait for
                            // a server snapshot before replacing the cache with nothing.
                            if (snapshot.metadata.isFromCache && items.isEmpty()) {
                                return@collect
                            }
                            replaceAll(items)
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            Log.w(TAG, "Failed to cache $collectionName snapshot", e)
                        }
                    }
            }
            try {
                emitAll(observeCached())
            } finally {
                listener.cancel()
            }
        }
    }

    /**
     * Single-document variant used by detail screens. Room remains the source
     * of truth: an existing document is written to Room, and a document the
     * server confirms is gone is removed from Room so the UI shows its
     * not-found state.
     */
    fun observeById(id: String): Flow<T?> {
        if (!isSafeDocumentId(id)) return flowOf(null)
        return flow {
            coroutineScope {
                val listener = launch {
                    documentSnapshots(id)
                        .catch { e -> Log.w(TAG, "Document listen failed for $collectionName/$id", e) }
                        .collect { snapshot ->
                            try {
                                when {
                                    snapshot.exists() -> upsert(listOf(mapper(snapshot)))
                                    // Don't delete a cached row just because Firestore's
                                    // own local cache does not know about it yet.
                                    !snapshot.metadata.isFromCache -> deleteById(id)
                                }
                            } catch (e: CancellationException) {
                                throw e
                            } catch (e: Exception) {
                                Log.w(TAG, "Failed to cache $collectionName/$id", e)
                            }
                        }
                }
                try {
                    emitAll(observeCachedById(id))
                } finally {
                    listener.cancel()
                }
            }
        }
    }

    /** Forces a server read, bypassing Firestore's own cache, then caches it. */
    suspend fun refresh() {
        val items = suspendCancellableCoroutine { cont ->
            firestore.collection(collectionName).get(Source.SERVER)
                .addOnSuccessListener { snapshot ->
                    cont.resume(snapshot.documents.map { mapper(it) }) { _, _, _ -> }
                }
                .addOnFailureListener { e -> cont.resumeWithException(e) }
        }
        replaceAll(items)
    }

    suspend fun clearCache() = clear()

    private fun collectionSnapshots(): Flow<QuerySnapshot> = callbackFlow {
        val registration = firestore.collection(collectionName)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Firestore listen failed for $collectionName", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) trySend(snapshot)
            }
        awaitClose { registration.remove() }
    }.conflate()

    private fun documentSnapshots(id: String): Flow<DocumentSnapshot> = callbackFlow {
        val registration = firestore.collection(collectionName).document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Document listen failed for $collectionName/$id", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) trySend(snapshot)
            }
        awaitClose { registration.remove() }
    }.conflate()

    private companion object {
        const val TAG = "AvalokanRepo"
    }
}
