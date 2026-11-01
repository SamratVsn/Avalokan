package com.example.avalokan.data.story

import com.example.avalokan.data.firestore.FirestoreCachedCollection
import com.example.avalokan.data.firestore.textField
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface StoryRepository {
    fun observeStories(): Flow<List<StoryItem>>
    fun observeStory(id: String): Flow<StoryItem?>
    suspend fun clearCache()
    suspend fun refresh()
}

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
        upsert = storyDao::upsertAll,
        clear = storyDao::clearAll,
        mapper = { it.toStoryItem() }
    )

    override fun observeStories(): Flow<List<StoryItem>> = collection.observeAll()
    override fun observeStory(id: String): Flow<StoryItem?> = collection.observeById(id)
    override suspend fun refresh() = collection.refresh()
    override suspend fun clearCache() = collection.clearCache()
}
