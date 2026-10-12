package com.example.avalokan.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.story.StoryItem
import com.example.avalokan.data.story.StoryRepository
import com.example.avalokan.ui.navigation.NavDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StoryDetailViewModel @Inject constructor(
    storyRepository: StoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val storyId: String =
        savedStateHandle.get<String>(NavDestination.StoryDetail.ARG).orEmpty()

    val story: StateFlow<StoryItem?> = storyRepository.observeStory(storyId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
