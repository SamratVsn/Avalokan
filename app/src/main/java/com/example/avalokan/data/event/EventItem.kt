package com.example.avalokan.data.event

import androidx.room3.Entity
import androidx.room3.PrimaryKey

//Defining the basic parameters for the events
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
//    Url for the image to show about the event
    val imageUrl: String? = null,
    val featured: Boolean = false,
//     Registration or ticket URL from Firestore (`joinLink`).
    val joinLink: String = ""
)