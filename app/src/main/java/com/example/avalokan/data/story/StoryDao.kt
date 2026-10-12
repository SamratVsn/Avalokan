package com.example.avalokan.data.story

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface StoryDao {
    @Query("SELECT * FROM stories")
    fun observeAll(): Flow<List<StoryItem>>

    @Query("SELECT * FROM stories WHERE id = :id")
    fun observeById(id: String): Flow<StoryItem?>

    @Upsert
    suspend fun upsertAll(items: List<StoryItem>)

    @Query("DELETE FROM stories")
    suspend fun clearAll()
}
