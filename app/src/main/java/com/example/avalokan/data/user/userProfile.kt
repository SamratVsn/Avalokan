package com.example.avalokan.data.user

data class UserProfile(
    val uid: String = "",
    val displayName: String = "Guest",
    val bio: String = "Exploring the heritage of Nepal",
    val avatarUrl: String? = null,
    val updatedAt: Long = 0L
)