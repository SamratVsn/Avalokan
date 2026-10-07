package com.example.avalokan.data.place

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {
    @Query("SELECT * FROM places")
    fun observeAll(): Flow<List<PlaceItem>>

    @Upsert
    suspend fun upsertAll(item: List<PlaceItem>)

    @Query("DELETE FROM places")
    suspend fun clearAll()
}