package com.example.avalokan.domain.model

//An editorial story as the UI understands it.
data class Story(
    val id: String,
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
    // Places referenced by this story.
    val placeIds: List<String> = emptyList(),
)
