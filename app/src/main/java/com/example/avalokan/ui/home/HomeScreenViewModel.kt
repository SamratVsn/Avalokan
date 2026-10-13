package com.example.avalokan.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.place.PlaceItem
import com.example.avalokan.data.place.PlaceRepository
import com.example.avalokan.data.story.StoryItem
import com.example.avalokan.data.story.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {
    /** Curated hero story document. */
    val heroStoryId: String = "boudhanath"

    /** Hero = whatever the stories collection holds (curated doc preferred). */
    val heroStory: StateFlow<StoryItem?> = storyRepository.observeStories()
        .map { stories ->
            stories.find { it.id == heroStoryId } ?: stories.firstOrNull()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val places: StateFlow<List<PlaceItem>> = placeRepository.observePlaces()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun refresh() {
        if (_isRefreshing.value) return
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                placeRepository.refresh()
                storyRepository.refresh()
            } catch (_: Exception) {
                // Snapshot listeners keep streaming; swipe just ends.
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
