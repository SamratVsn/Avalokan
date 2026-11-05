package com.example.avalokan.data.story

import androidx.room3.Entity
import androidx.room3.PrimaryKey

/**
 * Room persistence model. Map it to `domain.model.Story` at the repository
 * boundary; UI code should never depend on this type.
 */
@Entity(tableName = "stories")
data class StoryItem(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val author: String = "",
    val date: String = "",
    val readTime: String = "",
    val badge: String = "",
    // Editorial provenance. Blank means "not recorded", never "verified".
    val curator: String = "",
    val sourceLabel: String = "",
    val sourceUrl: String = "",
    val lastVerifiedAt: String = "",
    val verificationStatus: String = "",
    // Places referenced by this story, stored through the List<String> converter.
    val placeIds: List<String> = emptyList()
)
