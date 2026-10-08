package com.example.avalokan.data.event

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events")
    fun observeAll(): Flow<List<EventItem>>

    @Query("SELECT * FROM events WHERE id = :id")
    fun observeById(id: String): Flow<EventItem?>

    @Upsert //Upsert = Update or insert
    suspend fun upsertAll(items: List<EventItem>)

    @Query("DELETE FROM events")
    suspend fun clearAll()
}