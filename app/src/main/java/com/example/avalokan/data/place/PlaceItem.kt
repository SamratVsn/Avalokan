package com.example.avalokan.data.place

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

/**
 * Room persistence model. Map it to `domain.model.Place` at the repository
 * boundary; UI code should never depend on this type.
 */
@Entity(tableName = "places")
data class PlaceItem(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val meta: String = "",
    val category: String = "",
    val rating: String = "",
    val entryFee: String = "",
    val bestTime: String = "",
    val hours: String = "",
    val badge: String = "",
    val featured: Boolean = false,
    // Getting url to show the image, null if there's nothing in the database
    val imageUrl: String? = null,
    // Heritage context. Every field is optional so older documents keep working.
    val province: String = "",
    val district: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val era: String = "",
    // Free-form so values such as "World Heritage" or "Tentative List" survive.
    val unescoStatus: String = "",
    val heritageType: String = "",
    // Short editorial answer to "why does this place matter?".
    val significance: String = "",
    val livingHeritage: Boolean = false,
    val etiquette: String = "",
    val accessibility: String = "",
    // Provenance. Blank means "not recorded", never "verified".
    val sourceLabel: String = "",
    val sourceUrl: String = "",
    val lastVerifiedAt: String = "",
    val verificationStatus: String = "",
    // Multi-dimensional classification (see HeritageClassification.kt).
    @ColumnInfo(defaultValue = "''") val primaryCategory: String = "",
    @ColumnInfo(defaultValue = "''") val secondaryCategories: List<String> = emptyList(),
    @ColumnInfo(defaultValue = "''") val typologies: List<String> = emptyList(),
    @ColumnInfo(defaultValue = "''") val attributes: List<String> = emptyList(),
    @ColumnInfo(defaultValue = "''") val tags: List<String> = emptyList(),
    @ColumnInfo(defaultValue = "''") val locality: String = "",
    @ColumnInfo(defaultValue = "''") val relatedPlaceIds: List<String> = emptyList(),
    @ColumnInfo(defaultValue = "''") val relatedEventIds: List<String> = emptyList()
)
