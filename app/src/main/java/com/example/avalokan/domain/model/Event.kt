package com.example.avalokan.domain.model

import com.example.avalokan.domain.classification.EventFormat
import com.example.avalokan.domain.classification.EventKind
import com.example.avalokan.domain.classification.GeographicScope
import com.example.avalokan.domain.classification.Participation
import com.example.avalokan.domain.classification.PricingModel

//A heritage event as the UI understands it.
data class Event(
    val id: String,
    val title: String,
    val description: String,
    val meta: String = "",
    val fee: String = "",
    val action: String = "Join >",
    val type: String = "CULTURAL",
    val date: String = "",
    val imageUrl: String? = null,
    val featured: Boolean = false,
    val joinLink: String = "",
    // Classification. Empty values mean "not classified", not a guessed type.
    val eventKind: String = "",
    // Geographical reach of the event, not its venue. Stored as a stable key.
    val locality: String = "",
    // Pricing model: FREE / TICKETED / DONATION / UNKNOWN.
    val ticketing: String = "",
    // How the fee is charged (PER_PERSON, PER_GROUP, ...). Blank = unknown.
    val priceBasis: String = "",
    val participation: String = "",
    val format: String = "",
    val secondaryCategories: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    // Optional links to related heritage content.
    val placeId: String = "",
    val storyId: String = "",
    val relatedPlaceIds: List<String> = emptyList(),
    val relatedEventIds: List<String> = emptyList(),
    // Provenance. Blank means "not recorded", never "verified".
    val sourceLabel: String = "",
    val sourceUrl: String = "",
    val lastVerifiedAt: String = "",
    val verificationStatus: String = "",
) {
    val primaryKind: EventKind
        get() = EventKind.fromKey(eventKind) ?: EventKind.fromLegacyType(type)

    val geographicScope: GeographicScope
        get() = GeographicScope.fromKey(locality)

    val participationRequirement: Participation
        get() = Participation.fromKey(participation)

    val eventFormat: EventFormat?
        get() = EventFormat.fromKey(format)

    val pricing: PricingModel
        get() = PricingModel.fromKey(ticketing)

    /**
     * Featured presentation is only valid for explicitly classified festival
     * content. A technology event (or any unclassified event) must never gain
     * the featured treatment just because `featured` is true.
     */
    val isFeaturedEligible: Boolean
        get() = featured && primaryKind.isFestivalLike

    val hasValidJoinLink: Boolean
        get() = joinLink.trim().startsWith("http://", ignoreCase = true) ||
            joinLink.trim().startsWith("https://", ignoreCase = true)
}
