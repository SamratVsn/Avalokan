package com.example.avalokan.data.place

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {
    @Query("SELECT * FROM places ORDER BY id")
    fun observeAll(): Flow<List<PlaceItem>>

    @Query("SELECT * FROM places WHERE id = :id")
    fun observeById(id: String): Flow<PlaceItem?>

    @Upsert
    suspend fun upsertAll(item: List<PlaceItem>)

    @Query("DELETE FROM places")
    suspend fun clearAll()

    @Query("DELETE FROM places WHERE id = :id")
    suspend fun deleteById(id: String)

    /** Replaces the whole cache in one Room transaction, so observers see one update. */
    @Transaction
    suspend fun replaceAll(items: List<PlaceItem>) {
        clearAll()
        upsertAll(items)
    }
}
