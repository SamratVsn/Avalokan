package com.example.avalokan.data.story

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "stories")
data class StoryItem(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val author: String = "",
    val date: String = "",
    val readTime: String = "",
    val badge: String = ""
)
