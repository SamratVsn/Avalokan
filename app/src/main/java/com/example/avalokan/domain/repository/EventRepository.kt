package com.example.avalokan.domain.repository

import com.example.avalokan.domain.model.Event
import kotlinx.coroutines.flow.Flow

// Content contract used by the UI, independent of the Room cache.
interface EventRepository {
    fun observeEvents(): Flow<List<Event>>
    fun observeEvent(id: String): Flow<Event?>
    suspend fun clearCache()
    suspend fun refresh()
}
