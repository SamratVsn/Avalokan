package com.example.avalokan.data.event

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

interface EventRepository {
    fun observeEvents(): Flow<List<EventItem>>
}

private const val EVENTS_COLLECTION = "events"

class FirestoreEventRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val eventDao: EventDao
) : EventRepository {
    override fun observeEvents(): Flow<List<EventItem>> = callbackFlow {
        // Offline-first: emit cached Room rows first, then live Firestore updates.
        send(eventDao.observeAll().first())
        val registration = firestore.collection(EVENTS_COLLECTION)
            .addSnapshotListener { snapshot, _ ->
                val items = snapshot?.documents.orEmpty().map { doc ->
                    EventItem(
                        id = doc.id,
                        title = doc.getString("title").orEmpty(),
                        description = doc.getString("description").orEmpty(),
                        meta = doc.getString("meta").orEmpty(),
                        fee = doc.getString("fee").orEmpty(),
                        action = doc.getString("action").orEmpty()
                    )
                }
                launch { eventDao.upsertAll(items) }
                trySend(items)
            }
        awaitClose { registration.remove() }
    }
}