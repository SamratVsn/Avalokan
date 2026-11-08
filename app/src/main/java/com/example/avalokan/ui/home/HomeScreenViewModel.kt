package com.example.avalokan.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.domain.model.Place
import com.example.avalokan.domain.model.Story
import com.example.avalokan.domain.repository.PlaceRepository
import com.example.avalokan.domain.repository.StoryRepository
import com.example.avalokan.ui.common.ContentState
import com.example.avalokan.ui.common.asListContentState
import com.example.avalokan.ui.common.asItemContentState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
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

    /**
     * Bumping this restarts both content streams. The initial value starts the
     * normal subscription; retry uses it to pick the repository back up after
     * a failed first load.
     */
    private val reload = MutableStateFlow(0)

    /** Hero = whatever the stories collection holds (curated doc preferred). */
    val heroStoryState: StateFlow<ContentState<Story>> = reload
        .flatMapLatest {
            storyRepository.observeStories()
                .map { stories ->
                    stories.find { it.id == heroStoryId } ?: stories.firstOrNull()
                }
                .asItemContentState()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            ContentState.Loading
        )

    val placesState: StateFlow<ContentState<List<Place>>> = reload
        .flatMapLatest { placeRepository.observePlaces().asListContentState() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            ContentState.Loading
        )

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

    /** Restarts the streams and forces a server read after a failed load. */
    fun retry() {
        reload.value += 1
        refresh()
    }
}
