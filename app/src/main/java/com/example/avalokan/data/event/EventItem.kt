package com.example.avalokan.data.event

//import androidx.room3.Entity
//import androidx.room3.PrimaryKey

//@Entity(tableName = "Events")
data class EventItem(
//    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val category: String = "Personal",
)
