package com.example.avalokan.data.story

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

interface StoryRepository {
    fun observeStory(id: String): Flow<StoryItem?>
}

private const val STORIES_COLLECTION = "stories"

/** Console fields may be stored as String or Number — coerce either. */
private fun DocumentSnapshot.textField(name: String): String =
    get(name)?.toString().orEmpty()

class FirestoreStoryRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storyDao: StoryDao
) : StoryRepository {
    override fun observeStory(id: String): Flow<StoryItem?> = callbackFlow {
        send(storyDao.observeById(id).first())
        val registration = firestore.collection(STORIES_COLLECTION).document(id)
            .addSnapshotListener { snapshot, _ ->
                val item = snapshot?.let { doc ->
                    StoryItem(
                        id = doc.id,
                        title = doc.textField("title"),
                        subtitle = doc.textField("subTitle"),
                        description = doc.textField("description"),
                        imageUrl = doc.textField("imageUrl").ifBlank { null },
                        author = doc.textField("author"),
                        date = doc.textField("date"),
                        readTime = doc.textField("readtime"),
                        badge = doc.textField("badge")
                    )
                }
                if (item != null) {
                    launch { storyDao.upsertAll(listOf(item)) }
                    trySend(item)
                } else {
                    trySend(null)
                }
            }
        awaitClose { registration.remove() }
    }
}
