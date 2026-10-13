package com.example.avalokan.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.place.PlaceItem
import com.example.avalokan.data.place.PlaceRepository
import com.example.avalokan.data.story.StoryItem
import com.example.avalokan.data.story.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    placeRepository: PlaceRepository,
    storyRepository: StoryRepository
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
}
