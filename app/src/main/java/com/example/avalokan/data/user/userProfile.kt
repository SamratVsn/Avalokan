package com.example.avalokan.data.user

data class UserProfile(
    val uid: String = "",
    val displayName: String = "Guest",
    val bio: String = "Exploring the heritage of Nepal",
    /** Remote http(s) avatar URL only. Device-local avatars live in DataStore. */
    val avatarUrl: String? = null,
    val updatedAt: Long = 0L
)