package com.example.avalokan.data

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.example.avalokan.data.event.EventDao
import com.example.avalokan.data.event.EventItem
import com.example.avalokan.data.place.PlaceDao
import com.example.avalokan.data.place.PlaceItem

@Database(entities = [PlaceItem::class, EventItem::class], version = 2)
abstract class AvalokanDatabase : RoomDatabase() {
    abstract fun placeDao(): PlaceDao
    abstract fun eventDao(): EventDao
}