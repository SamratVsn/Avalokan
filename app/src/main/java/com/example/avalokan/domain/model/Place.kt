package com.example.avalokan.domain.model

//A heritage place as the UI understands it.
data class Place(
    val id: String,
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
    val imageUrl: String? = null,
    // Heritage context. Every field is optional so older documents keep working.
    val province: String = "",
    val district: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val era: String = "",
    val unescoStatus: String = "",
    val heritageType: String = "",
    val significance: String = "",
    val livingHeritage: Boolean = false,
    val etiquette: String = "",
    val accessibility: String = "",
    // Provenance. Blank means "not recorded", never "verified".
    val sourceLabel: String = "",
    val sourceUrl: String = "",
    val lastVerifiedAt: String = "",
    val verificationStatus: String = "",
)
