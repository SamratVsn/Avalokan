package com.example.avalokan.domain.repository

import com.example.avalokan.domain.model.Story
import kotlinx.coroutines.flow.Flow

// Content contract used by the UI, independent of the Room cache.
interface StoryRepository {
    fun observeStories(): Flow<List<Story>>
    fun observeStory(id: String): Flow<Story?>
    suspend fun clearCache()
    suspend fun refresh()
}
