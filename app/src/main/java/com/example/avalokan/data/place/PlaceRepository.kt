package com.example.avalokan.data.place

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
}

private const val PLACES_COLLECTION = "places"

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
                        name = doc.getString("name").orEmpty(),
                        description = doc.getString("description").orEmpty(),
                        meta = doc.getString("meta").orEmpty(),
                        category = doc.getString("category").orEmpty(),
                        rating = doc.getString("rating").orEmpty()
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
                        name = doc.getString("name").orEmpty(),
                        description = doc.getString("description").orEmpty(),
                        meta = doc.getString("meta").orEmpty(),
                        category = doc.getString("category").orEmpty(),
                        rating = doc.getString("rating").orEmpty()
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
}