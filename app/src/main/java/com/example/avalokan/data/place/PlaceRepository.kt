package com.example.avalokan.data.place

import com.example.avalokan.data.firestore.FirestoreCachedCollection
import com.example.avalokan.data.firestore.textField
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface PlaceRepository {
    fun observePlaces(): Flow<List<PlaceItem>>
    fun observePlace(id: String): Flow<PlaceItem?>
    suspend fun clearCache()
    /** Forces a server fetch; snapshot listeners keep streaming afterwards. */
    suspend fun refresh()
}

private const val PLACES_COLLECTION = "places"

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
    firestore: FirebaseFirestore,
    placeDao: PlaceDao
) : PlaceRepository {
    private val collection = FirestoreCachedCollection(
        firestore = firestore,
        collectionName = PLACES_COLLECTION,
        observeCached = placeDao::observeAll,
        observeCachedById = placeDao::observeById,
        replaceAll = placeDao::replaceAll,
        upsert = placeDao::upsertAll,
        deleteById = placeDao::deleteById,
        clear = placeDao::clearAll,
        mapper = { it.toPlaceItem() }
    )

    override fun observePlaces(): Flow<List<PlaceItem>> = collection.observeAll()
    override fun observePlace(id: String): Flow<PlaceItem?> = collection.observeById(id)
    override suspend fun refresh() = collection.refresh()
    override suspend fun clearCache() = collection.clearCache()
}
