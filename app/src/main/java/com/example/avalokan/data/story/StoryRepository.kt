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

package com.example.avalokan.data.story

import com.example.avalokan.data.classification.LegacyClassificationCatalog
import com.example.avalokan.data.firestore.FirestoreCachedCollection
import com.example.avalokan.data.firestore.asStringList
import com.example.avalokan.data.firestore.asText
import com.example.avalokan.domain.model.Story
import com.example.avalokan.domain.repository.StoryRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val STORIES_COLLECTION = "stories"

/**
 * Pure mapping step, kept separate from [DocumentSnapshot] so missing-field
 * behaviour can be unit-tested without Firestore.
 */
internal fun toStoryItem(id: String, field: (String) -> Any?): StoryItem {
    val seed = LegacyClassificationCatalog.story(id)
    return StoryItem(
        id = id,
        title = field("title").asText(),
        subtitle = field("subTitle").asText(),
        description = field("description").asText(),
        imageUrl = field("imageUrl").asText().ifBlank { null },
        author = field("author").asText(),
        date = field("date").asText(),
        readTime = field("readtime").asText(),
        badge = field("badge").asText(),
        curator = field("curator").asText(),
        sourceLabel = field("sourceLabel").asText(),
        sourceUrl = field("sourceUrl").asText(),
        lastVerifiedAt = field("lastVerifiedAt").asText(),
        verificationStatus = field("verificationStatus").asText(),
        placeIds = field("placeIds").asStringList().ifEmpty { seed?.placeIds.orEmpty() },
        primaryCategory = field("primaryCategory").asText().ifBlank { seed?.primaryCategory.orEmpty() },
        secondaryCategories = field("secondaryCategories").asStringList()
            .ifEmpty { seed?.secondaryCategories.orEmpty() },
        typologies = field("typologies").asStringList().ifEmpty { seed?.typologies.orEmpty() },
        tags = field("tags").asStringList().ifEmpty { seed?.tags.orEmpty() },
        relatedEventIds = field("relatedEventIds").asStringList()
            .ifEmpty { seed?.relatedEventIds.orEmpty() },
        period = field("period").asText().ifBlank { seed?.period.orEmpty() }
    )
}

private fun DocumentSnapshot.toStoryItem() = toStoryItem(id) { get(it) }

class FirestoreStoryRepository @Inject constructor(
    firestore: FirebaseFirestore,
    storyDao: StoryDao
) : StoryRepository {
    private val collection = FirestoreCachedCollection(
        firestore = firestore,
        collectionName = STORIES_COLLECTION,
        observeCached = storyDao::observeAll,
        observeCachedById = storyDao::observeById,
        replaceAll = storyDao::replaceAll,
        upsert = storyDao::upsertAll,
        deleteById = storyDao::deleteById,
        clear = storyDao::clearAll,
        mapper = { it.toStoryItem() }
    )

    override fun observeStories(): Flow<List<Story>> = collection.observeAll()
        .map { items -> items.map { it.toDomain() } }

    override fun observeStory(id: String): Flow<Story?> = collection.observeById(id)
        .map { it?.toDomain() }

    override suspend fun refresh() = collection.refresh()
    override suspend fun clearCache() = collection.clearCache()
}
