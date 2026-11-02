package com.example.avalokan.data.story

import com.example.avalokan.data.firestore.FirestoreCachedCollection
import com.example.avalokan.data.firestore.textField
import com.example.avalokan.domain.model.Story
import com.example.avalokan.domain.repository.StoryRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val STORIES_COLLECTION = "stories"

private fun DocumentSnapshot.toStoryItem() = StoryItem(
    id = id,
    title = textField("title"),
    subtitle = textField("subTitle"),
    description = textField("description"),
    imageUrl = textField("imageUrl").ifBlank { null },
    author = textField("author"),
    date = textField("date"),
    readTime = textField("readtime"),
    badge = textField("badge")
)

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
