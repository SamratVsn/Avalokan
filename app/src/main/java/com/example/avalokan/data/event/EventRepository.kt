/*
 * Copyright (C) 2026 Samrat Parajuli
 *
 * This file is part of Avalokan.
 *
 * Avalokan is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * Avalokan is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Avalokan. If not, see <https://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */

package com.example.avalokan.data.event

import com.example.avalokan.data.classification.LegacyClassificationCatalog
import com.example.avalokan.data.firestore.FirestoreCachedCollection
import com.example.avalokan.data.firestore.asStringList
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
internal fun toEventItem(id: String, field: (String) -> Any?): EventItem {
    val seed = LegacyClassificationCatalog.event(id)
    return EventItem(
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
        eventKind = field("eventKind").asText().ifBlank { seed?.eventKind.orEmpty() },
        locality = field("locality").asText().ifBlank { seed?.locality.orEmpty() },
        ticketing = field("ticketing").asText().ifBlank { seed?.ticketing.orEmpty() },
        priceBasis = field("priceBasis").asText().ifBlank { seed?.priceBasis.orEmpty() },
        participation = field("participation").asText().ifBlank { seed?.participation.orEmpty() },
        format = field("format").asText().ifBlank { seed?.format.orEmpty() },
        secondaryCategories = field("secondaryCategories").asStringList()
            .ifEmpty { seed?.secondaryCategories.orEmpty() },
        tags = field("tags").asStringList().ifEmpty { seed?.tags.orEmpty() },
        placeId = field("placeId").asText(),
        storyId = field("storyId").asText(),
        relatedPlaceIds = field("relatedPlaceIds").asStringList()
            .ifEmpty { seed?.relatedPlaceIds.orEmpty() },
        relatedEventIds = field("relatedEventIds").asStringList(),
        sourceLabel = field("sourceLabel").asText(),
        sourceUrl = field("sourceUrl").asText(),
        lastVerifiedAt = field("lastVerifiedAt").asText(),
        verificationStatus = field("verificationStatus").asText()
    )
}

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
