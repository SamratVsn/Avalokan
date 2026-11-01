package com.example.avalokan.data.event

import com.example.avalokan.data.firestore.FirestoreCachedCollection
import com.example.avalokan.data.firestore.textField
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface EventRepository {
    fun observeEvents(): Flow<List<EventItem>>
    fun observeEvent(id: String): Flow<EventItem?>
    suspend fun clearCache()
    suspend fun refresh()
}

private const val EVENTS_COLLECTION = "events"

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
    firestore: FirebaseFirestore,
    eventDao: EventDao
) : EventRepository {
    private val collection = FirestoreCachedCollection(
        firestore = firestore,
        collectionName = EVENTS_COLLECTION,
        observeCached = eventDao::observeAll,
        observeCachedById = eventDao::observeById,
        replaceAll = eventDao::replaceAll,
        upsert = eventDao::upsertAll,
        deleteById = eventDao::deleteById,
        clear = eventDao::clearAll,
        mapper = { it.toEventItem() }
    )

    override fun observeEvents(): Flow<List<EventItem>> = collection.observeAll()
    override fun observeEvent(id: String): Flow<EventItem?> = collection.observeById(id)
    override suspend fun refresh() = collection.refresh()
    override suspend fun clearCache() = collection.clearCache()
}
