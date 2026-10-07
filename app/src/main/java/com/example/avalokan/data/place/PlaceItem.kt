package com.example.avalokan.data.place

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "places")
data class PlaceItem(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val meta: String = "",
    val category: String = "",
    val rating: String = ""
)