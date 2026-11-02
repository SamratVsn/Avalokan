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
)
