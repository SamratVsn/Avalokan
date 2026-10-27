package com.example.avalokan.data.story

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

interface StoryRepository {
    fun observeStories(): Flow<List<StoryItem>>
    fun observeStory(id: String): Flow<StoryItem?>
    suspend fun refresh()
}

private const val STORIES_COLLECTION = "stories"

/** Console fields may be stored as String or Number — coerce either. */
private fun DocumentSnapshot.textField(name: String): String =
    get(name)?.toString().orEmpty()

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
    private val firestore: FirebaseFirestore,
    private val storyDao: StoryDao
) : StoryRepository {
    override fun observeStories(): Flow<List<StoryItem>> = callbackFlow {
        send(storyDao.observeAll().first())
        val registration = firestore.collection(STORIES_COLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Keep the last good emission rather than blanking the UI.
                    Log.w("AvalokanRepo", "Firestore listen failed", error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents.orEmpty().map { it.toStoryItem() }
                launch { storyDao.upsertAll(items) }
                trySend(items)
            }
        awaitClose { registration.remove() }
    }

    override fun observeStory(id: String): Flow<StoryItem?> {
        if (!isSafeDocumentId(id)) return flowOf(null)
        return callbackFlow {
        send(storyDao.observeById(id).first())
        val registration = firestore.collection(STORIES_COLLECTION).document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Keep the last good emission rather than reporting "not found".
                    Log.w("AvalokanRepo", "Firestore listen failed", error)
                    return@addSnapshotListener
                }
                // A document that does not exist must resolve to null, otherwise
                // the story screen falls back to placeholder copy for a story
                // that does not exist.
                val item = snapshot?.takeIf { it.exists() }?.toStoryItem()
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

    override suspend fun refresh() {
        val items = suspendCancellableCoroutine { cont ->
            firestore.collection(STORIES_COLLECTION).get(Source.SERVER)
                .addOnSuccessListener { snapshot ->
                    cont.resume(snapshot.documents.map { it.toStoryItem() }) {}
                }
                .addOnFailureListener { e -> cont.resumeWithException(e) }
        }
        storyDao.upsertAll(items)
    }
}
