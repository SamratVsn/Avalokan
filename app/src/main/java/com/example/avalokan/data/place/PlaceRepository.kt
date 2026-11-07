package com.example.avalokan.data.place

import com.example.avalokan.data.classification.LegacyClassificationCatalog
import com.example.avalokan.data.firestore.FirestoreCachedCollection
import com.example.avalokan.data.firestore.asDoubleOrNull
import com.example.avalokan.data.firestore.asStringList
import com.example.avalokan.data.firestore.asText
import com.example.avalokan.domain.model.Place
import com.example.avalokan.domain.repository.PlaceRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val PLACES_COLLECTION = "places"

/**
 * Pure mapping step, kept separate from [DocumentSnapshot] so missing-field
 * behaviour can be unit-tested without Firestore.
 */
internal fun toPlaceItem(id: String, field: (String) -> Any?): PlaceItem {
    val seed = LegacyClassificationCatalog.place(id)
    return PlaceItem(
        id = id,
        name = field("name").asText(),
        description = field("description").asText(),
        meta = field("meta").asText(),
        category = field("category").asText(),
        rating = field("rating").asText(),
        featured = field("featured") == true,
        badge = field("badge").asText(),
        entryFee = field("entryFee").asText(),
        bestTime = field("bestTime").asText(),
        hours = field("hours").asText(),
        imageUrl = field("imageUrl").asText().ifBlank { null },
        province = field("province").asText(),
        district = field("district").asText(),
        latitude = field("latitude").asDoubleOrNull(),
        longitude = field("longitude").asDoubleOrNull(),
        era = field("era").asText(),
        unescoStatus = field("unescoStatus").asText().ifBlank { seed?.unescoStatus.orEmpty() },
        heritageType = field("heritageType").asText().ifBlank { seed?.heritageType.orEmpty() },
        significance = field("significance").asText(),
        livingHeritage = field("livingHeritage") == true || seed?.livingHeritage == true,
        etiquette = field("etiquette").asText(),
        accessibility = field("accessibility").asText(),
        primaryCategory = field("primaryCategory").asText().ifBlank { seed?.primaryCategory.orEmpty() },
        secondaryCategories = field("secondaryCategories").asStringList()
            .ifEmpty { seed?.secondaryCategories.orEmpty() },
        typologies = field("typologies").asStringList().ifEmpty { seed?.typologies.orEmpty() },
        attributes = field("attributes").asStringList().ifEmpty { seed?.attributes.orEmpty() },
        tags = field("tags").asStringList().ifEmpty { seed?.tags.orEmpty() },
        locality = field("locality").asText().ifBlank { seed?.locality.orEmpty() },
        relatedPlaceIds = field("relatedPlaceIds").asStringList(),
        relatedEventIds = field("relatedEventIds").asStringList(),
        sourceLabel = field("sourceLabel").asText(),
        sourceUrl = field("sourceUrl").asText(),
        lastVerifiedAt = field("lastVerifiedAt").asText(),
        verificationStatus = field("verificationStatus").asText()
    )
}

private fun DocumentSnapshot.toPlaceItem() = toPlaceItem(id) { get(it) }

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

    override fun observePlaces(): Flow<List<Place>> = collection.observeAll()
        .map { items -> items.map { it.toDomain() } }

    override fun observePlace(id: String): Flow<Place?> = collection.observeById(id)
        .map { it?.toDomain() }

    override suspend fun refresh() = collection.refresh()
    override suspend fun clearCache() = collection.clearCache()
}
