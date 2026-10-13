package com.example.avalokan.data

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.example.avalokan.data.event.EventDao
import com.example.avalokan.data.event.EventItem
import com.example.avalokan.data.place.PlaceDao
import com.example.avalokan.data.place.PlaceItem
import com.example.avalokan.data.story.StoryDao
import com.example.avalokan.data.story.StoryItem

@Database(entities = [PlaceItem::class, EventItem::class, StoryItem::class], version = 7)
abstract class AvalokanDatabase : RoomDatabase() {
    abstract fun placeDao(): PlaceDao
    abstract fun eventDao(): EventDao
    abstract fun storyDao(): StoryDao
}