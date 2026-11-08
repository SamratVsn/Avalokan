package com.example.avalokan.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.domain.model.Story
import com.example.avalokan.domain.repository.StoryRepository
import com.example.avalokan.ui.common.ContentState
import com.example.avalokan.ui.common.asItemContentState
import com.example.avalokan.ui.navigation.NavDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryDetailViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val storyId: String =
        savedStateHandle.get<String>(NavDestination.StoryDetail.ARG).orEmpty()

    private val reload = MutableStateFlow(0)

    val storyState: StateFlow<ContentState<Story>> = reload
        .flatMapLatest { storyRepository.observeStory(storyId).asItemContentState() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            ContentState.Loading
        )

    /** Restarts the item stream and forces a server read. */
    fun retry() {
        reload.value += 1
        viewModelScope.launch {
            try {
                storyRepository.refresh()
            } catch (_: Exception) {
                // Snapshot listeners keep streaming; the retry just ends.
            }
        }
    }
}
