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
)
