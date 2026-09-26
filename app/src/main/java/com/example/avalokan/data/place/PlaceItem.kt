package com.example.avalokan.data.place

//import androidx.room3.Entity
//import androidx.room3.PrimaryKey

//@Entity(tableName = "Places")
data class PlaceItem(
//    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val category: String = "",
)