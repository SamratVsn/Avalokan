package com.example.avalokan.data.story

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface StoryDao {
    @Query("SELECT * FROM stories ORDER BY id")
    fun observeAll(): Flow<List<StoryItem>>

    @Query("SELECT * FROM stories WHERE id = :id")
    fun observeById(id: String): Flow<StoryItem?>

    @Upsert
    suspend fun upsertAll(items: List<StoryItem>)

    @Query("DELETE FROM stories")
    suspend fun clearAll()

    @Query("DELETE FROM stories WHERE id = :id")
    suspend fun deleteById(id: String)

    /** Replaces the whole cache in one Room transaction, so observers see one update. */
    @Transaction
    suspend fun replaceAll(items: List<StoryItem>) {
        clearAll()
        upsertAll(items)
    }
}
