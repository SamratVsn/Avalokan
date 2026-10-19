package com.example.avalokan.data.place

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import javax.inject.Inject
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

interface PlaceRepository {
    fun observePlaces(): Flow<List<PlaceItem>>
    fun observePlace(id: String): Flow<PlaceItem?>
    suspend fun clearCache()
    /** Forces a server fetch; snapshot listeners keep streaming afterwards. */
    suspend fun refresh()
}

private const val PLACES_COLLECTION = "places"

/** Console fields may be stored as String or Number — coerce either. */
private fun DocumentSnapshot.textField(name: String): String =
    get(name)?.toString().orEmpty()

private fun DocumentSnapshot.toPlaceItem() = PlaceItem(
    id = id,
    name = textField("name"),
    description = textField("description"),
    meta = textField("meta"),
    category = textField("category"),
    rating = textField("rating"),
    featured = getBoolean("featured") == true,
    badge = textField("badge"),
    entryFee = textField("entryFee"),
    bestTime = textField("bestTime"),
    hours = textField("hours"),
    imageUrl = textField("imageUrl").ifBlank { null }
)

class FirestorePlaceRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val placeDao: PlaceDao
) : PlaceRepository {
    override fun observePlaces(): Flow<List<PlaceItem>> = callbackFlow{
        //Offline-first:
        send(placeDao.observeAll().first())
        val registration = firestore.collection(PLACES_COLLECTION)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    // Keep the last good emission rather than blanking the UI.
                    Log.w("AvalokanRepo", "Firestore listen failed", error)
                    return@addSnapshotListener
                }
                val items = snapshots?.documents.orEmpty().map { it.toPlaceItem() }
                launch { placeDao.upsertAll(items)}
                trySend(items)
            }
        awaitClose { registration.remove() }
    }

    override fun observePlace(id: String): Flow<PlaceItem?> = callbackFlow {
        send(placeDao.observeById(id).first())
        val registration = firestore.collection(PLACES_COLLECTION).document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Keep the last good emission rather than reporting "not found".
                    Log.w("AvalokanRepo", "Firestore listen failed", error)
                    return@addSnapshotListener
                }
                // A document that does not exist must resolve to null, otherwise
                // detail screens render a blank entity instead of their not-found state.
                val item = snapshot?.takeIf { it.exists() }?.toPlaceItem()
                if (item != null) {
                    launch { placeDao.upsertAll(listOf(item)) }
                    trySend(item)
                } else {
                    trySend(null)
                }
            }
        awaitClose { registration.remove() }
    }

    override suspend fun refresh() {
        val items = suspendCancellableCoroutine { cont ->
            firestore.collection(PLACES_COLLECTION).get(Source.SERVER)
                .addOnSuccessListener { snapshot ->
                    cont.resume(snapshot.documents.map { it.toPlaceItem() }) {}
                }
                .addOnFailureListener { e -> cont.resumeWithException(e) }
        }
        placeDao.upsertAll(items)
    }

    override suspend fun clearCache() {
        placeDao.clearAll()
    }
}