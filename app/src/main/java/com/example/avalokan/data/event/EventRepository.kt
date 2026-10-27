package com.example.avalokan.data.event

import com.example.avalokan.data.security.isSafeDocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

interface EventRepository {
    fun observeEvents(): Flow<List<EventItem>>
    fun observeEvent(id: String): Flow<EventItem?>
    suspend fun clearCache()
    suspend fun refresh()
}

private const val EVENTS_COLLECTION = "events"

/** Console fields may be stored as String or Number — coerce either. */
private fun DocumentSnapshot.textField(name: String): String =
    get(name)?.toString().orEmpty()

private fun DocumentSnapshot.toEventItem() = EventItem(
    id = id,
    title = textField("title"),
    description = textField("description"),
    meta = textField("meta"),
    fee = textField("fee"),
    action = textField("action").ifBlank { "Join >" },
    type = textField("type").ifBlank { "CULTURAL" },
    featured = getBoolean("featured") == true,
    date = textField("date"),
    imageUrl = textField("imageUrl").ifBlank { null },
    joinLink = textField("joinLink").ifBlank { textField("onJoinClick") }
)

class FirestoreEventRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val eventDao: EventDao
) : EventRepository {
    override fun observeEvents(): Flow<List<EventItem>> = callbackFlow {
        // Offline-first: emit cached Room rows first, then live Firestore updates.
        send(eventDao.observeAll().first())
        val registration = firestore.collection(EVENTS_COLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Keep the last good emission rather than blanking the UI.
                    Log.w("AvalokanRepo", "Firestore listen failed", error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents.orEmpty().map { doc ->
                    doc.toEventItem()
                }
                launch { eventDao.upsertAll(items) }
                trySend(items)
            }
        awaitClose { registration.remove() }
    }

    override fun observeEvent(id: String): Flow<EventItem?> {
        if (!isSafeDocumentId(id)) return flowOf(null)
        return callbackFlow {
        send(eventDao.observeById(id).first())
        val registration = firestore.collection(EVENTS_COLLECTION).document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Keep the last good emission rather than reporting "not found".
                    Log.w("AvalokanRepo", "Firestore listen failed", error)
                    return@addSnapshotListener
                }
                // A document that does not exist must resolve to null, otherwise
                // the detail screen renders a blank entity instead of its not-found state.
                val item = snapshot?.takeIf { it.exists() }?.toEventItem()
                if (item != null) {
                    launch { eventDao.upsertAll(listOf(item)) }
                    trySend(item)
                } else {
                    trySend(null)
                }
            }
        awaitClose { registration.remove() }
        }
    }

    override suspend fun clearCache() {
        eventDao.clearAll()
    }

    override suspend fun refresh() {
        val items = suspendCancellableCoroutine { cont ->
            firestore.collection(EVENTS_COLLECTION).get(Source.SERVER)
                .addOnSuccessListener { snapshot ->
                    cont.resume(snapshot.documents.map { it.toEventItem() }) {}
                }
                .addOnFailureListener { e -> cont.resumeWithException(e) }
        }
        eventDao.upsertAll(items)
    }
}