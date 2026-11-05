package com.example.avalokan.data.event

import com.example.avalokan.data.firestore.FirestoreCachedCollection
import com.example.avalokan.data.firestore.asText
import com.example.avalokan.domain.model.Event
import com.example.avalokan.domain.repository.EventRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val EVENTS_COLLECTION = "events"

/**
 * Pure mapping step, kept separate from [DocumentSnapshot] so missing-field
 * behaviour can be unit-tested without Firestore.
 */
internal fun toEventItem(id: String, field: (String) -> Any?): EventItem = EventItem(
    id = id,
    title = field("title").asText(),
    description = field("description").asText(),
    meta = field("meta").asText(),
    fee = field("fee").asText(),
    action = field("action").asText().ifBlank { "Join >" },
    type = field("type").asText().ifBlank { "CULTURAL" },
    featured = field("featured") == true,
    date = field("date").asText(),
    imageUrl = field("imageUrl").asText().ifBlank { null },
    joinLink = field("joinLink").asText().ifBlank { field("onJoinClick").asText() },
    eventKind = field("eventKind").asText(),
    locality = field("locality").asText(),
    ticketing = field("ticketing").asText(),
    placeId = field("placeId").asText(),
    storyId = field("storyId").asText(),
    sourceLabel = field("sourceLabel").asText(),
    sourceUrl = field("sourceUrl").asText(),
    lastVerifiedAt = field("lastVerifiedAt").asText(),
    verificationStatus = field("verificationStatus").asText()
)

private fun DocumentSnapshot.toEventItem() = toEventItem(id) { get(it) }

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

    override fun observeEvents(): Flow<List<Event>> = collection.observeAll()
        .map { items -> items.map { it.toDomain() } }

    override fun observeEvent(id: String): Flow<Event?> = collection.observeById(id)
        .map { it?.toDomain() }

    override suspend fun refresh() = collection.refresh()
    override suspend fun clearCache() = collection.clearCache()
}
