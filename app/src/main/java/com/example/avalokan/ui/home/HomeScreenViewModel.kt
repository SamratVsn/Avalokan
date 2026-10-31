package com.example.avalokan.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.place.PlaceItem
import com.example.avalokan.data.place.PlaceRepository
import com.example.avalokan.data.story.StoryItem
import com.example.avalokan.data.story.StoryRepository
import com.example.avalokan.ui.common.CONTENT_LOADING_TIMEOUT_MS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
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

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            // Stop the skeleton on a cold, empty, offline start too. A warm
            // cache clears this almost instantly through the flows below.
            delay(CONTENT_LOADING_TIMEOUT_MS)
            _isLoading.value = false
        }
    }

    /** Hero = whatever the stories collection holds (curated doc preferred). */
    val heroStory: StateFlow<StoryItem?> = storyRepository.observeStories()
        .map { stories ->
            stories.find { it.id == heroStoryId } ?: stories.firstOrNull()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val places: StateFlow<List<PlaceItem>> = placeRepository.observePlaces()
        .onEach { places ->
            if (places.isNotEmpty()) _isLoading.value = false
        }
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
                _isLoading.value = false
            }
        }
    }
}
