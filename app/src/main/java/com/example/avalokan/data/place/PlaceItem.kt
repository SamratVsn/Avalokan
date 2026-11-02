package com.example.avalokan.data.place

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
    val imageUrl: String? = null
)
