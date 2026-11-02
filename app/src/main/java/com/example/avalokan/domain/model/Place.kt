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
)
