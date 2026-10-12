package com.example.avalokan.data.event

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "events")
data class EventItem(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val meta: String = "",
    val fee: String = "",
    val action: String = "Join >",
    val type: String = "CULTURAL",
    val date: String = "",
    val imageUrl: String? = null,
    val featured: Boolean? = false
)