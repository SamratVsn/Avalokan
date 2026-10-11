package com.example.avalokan.data.place

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

interface PlaceRepository {
    fun observePlaces(): Flow<List<PlaceItem>>
    fun observePlace(id: String): Flow<PlaceItem?>
    suspend fun clearCache()
}

private const val PLACES_COLLECTION = "places"

/** Console fields may be stored as String or Number — coerce either. */
private fun DocumentSnapshot.textField(name: String): String =
    get(name)?.toString().orEmpty()

class FirestorePlaceRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val placeDao: PlaceDao
) : PlaceRepository {
    override fun observePlaces(): Flow<List<PlaceItem>> = callbackFlow{
        //Offline-first:
        send(placeDao.observeAll().first())
        val registration = firestore.collection(PLACES_COLLECTION)
            .addSnapshotListener { snapshots, _ ->
                val items = snapshots?.documents.orEmpty().map { doc ->
                    PlaceItem(
                        id = doc.id,
                        name = doc.textField("name"),
                        description = doc.textField("description"),
                        meta = doc.textField("meta"),
                        category = doc.textField("category"),
                        rating = doc.textField("rating"),
                        badge = doc.textField("badge"),
                        entryFee = doc.textField("entryFee"),
                        bestTime = doc.textField("bestTime"),
                        hours = doc.textField("hours"),
                        imageUrl = doc.textField("imageUrl").ifBlank { null }
                    )
                }
                launch { placeDao.upsertAll(items)}
                trySend(items)
            }
        awaitClose { registration.remove() }
    }

    override fun observePlace(id: String): Flow<PlaceItem?> = callbackFlow {
        send(placeDao.observeById(id).first())
        val registration = firestore.collection(PLACES_COLLECTION).document(id)
            .addSnapshotListener { snapshot, _ ->
                val item = snapshot?.let { doc ->
                    PlaceItem(
                        id = doc.id,
                        name = doc.textField("name"),
                        description = doc.textField("description"),
                        meta = doc.textField("meta"),
                        category = doc.textField("category"),
                        rating = doc.textField("rating"),
                        badge = doc.textField("badge"),
                        entryFee = doc.textField("entryFee"),
                        bestTime = doc.textField("bestTime"),
                        hours = doc.textField("hours"),
                        imageUrl = doc.textField("imageUrl").ifBlank { null }
                    )
                }
                if (item != null) {
                    launch { placeDao.upsertAll(listOf(item)) }
                    trySend(item)
                } else {
                    trySend(null)
                }
            }
        awaitClose { registration.remove() }
    }

    override suspend fun clearCache() {
        placeDao.clearAll()
    }
}