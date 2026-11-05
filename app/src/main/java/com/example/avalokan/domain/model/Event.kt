package com.example.avalokan.domain.model

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
    val locality: String = "",
    val ticketing: String = "",
    // Optional links to related heritage content.
    val placeId: String = "",
    val storyId: String = "",
    // Provenance. Blank means "not recorded", never "verified".
    val sourceLabel: String = "",
    val sourceUrl: String = "",
    val lastVerifiedAt: String = "",
    val verificationStatus: String = "",
)
