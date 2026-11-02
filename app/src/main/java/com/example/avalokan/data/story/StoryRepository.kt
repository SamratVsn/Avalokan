package com.example.avalokan.data.story

import com.example.avalokan.data.firestore.FirestoreCachedCollection
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
internal fun toStoryItem(id: String, field: (String) -> Any?): StoryItem = StoryItem(
    id = id,
    title = field("title").asText(),
    subtitle = field("subTitle").asText(),
    description = field("description").asText(),
    imageUrl = field("imageUrl").asText().ifBlank { null },
    author = field("author").asText(),
    date = field("date").asText(),
    readTime = field("readtime").asText(),
    badge = field("badge").asText()
)

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
