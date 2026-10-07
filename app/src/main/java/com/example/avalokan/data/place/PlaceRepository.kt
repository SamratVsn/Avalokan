package com.example.avalokan.data.place

import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

interface PlaceRepository {
    fun observePlaces() : Flow<List<PlaceItem>>
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
}