package com.example.avalokan.domain.model

import com.example.avalokan.domain.classification.HeritageTypology
import com.example.avalokan.domain.classification.StoryCategory

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
    // Editorial classification, independent from place/event categories.
    val primaryCategory: String = "",
    val secondaryCategories: List<String> = emptyList(),
    val typologies: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val relatedEventIds: List<String> = emptyList(),
    // Only populated when a historical period is actually verified.
    val period: String = "",
) {
    val primary: StoryCategory?
        get() = StoryCategory.fromKey(primaryCategory)

    val heritageTypologies: List<HeritageTypology>
        get() = HeritageTypology.fromKeys(typologies)
}
