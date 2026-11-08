package com.example.avalokan.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.local.PreferencesStore
import com.example.avalokan.domain.model.Place
import com.example.avalokan.domain.repository.PlaceRepository
import com.example.avalokan.ui.common.ContentState
import com.example.avalokan.ui.common.asItemContentState
import com.example.avalokan.ui.navigation.NavDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceDetailViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val userPreferences: PreferencesStore,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val placeId: String =
        savedStateHandle.get<String>(NavDestination.PlaceDetail.ARG).orEmpty()

    private val reload = MutableStateFlow(0)

    val placeState: StateFlow<ContentState<Place>> = reload
        .flatMapLatest { placeRepository.observePlace(placeId).asItemContentState() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            ContentState.Loading
        )

    val savedIds: StateFlow<Set<String>> = userPreferences.savedPlaceIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    val visitedIds: StateFlow<Set<String>> = userPreferences.visitedPlaceIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    private val _events = MutableSharedFlow<PlaceDetailEvent>()
    val events: SharedFlow<PlaceDetailEvent> = _events.asSharedFlow()

    /** Toggles save; returns true when the place is now saved. */
    fun toggleSave(): Boolean {
        val current = savedIds.value.toMutableSet()
        val nowSaved = if (!current.add(placeId)) {
            current.remove(placeId)
            false
        } else true
        viewModelScope.launch { userPreferences.setSavedPlaceIds(current) }
        return nowSaved
    }

    /**
     * Explicit visit toggle. This is the only writer to the visited set, so a
     * detail-screen view can never be mistaken for an actual visit.
     */
    fun toggleVisited(): Boolean {
        val current = visitedIds.value.toMutableSet()
        val nowVisited = if (!current.add(placeId)) {
            current.remove(placeId)
            false
        } else true
        viewModelScope.launch { userPreferences.setVisitedPlaceIds(current) }
        return nowVisited
    }

    /** Restarts the item stream and forces a server read. */
    fun retry() {
        reload.value += 1
        viewModelScope.launch {
            try {
                placeRepository.refresh()
            } catch (_: Exception) {
                // Snapshot listeners keep streaming; the retry just ends.
            }
        }
    }

    fun onBackClick() {
        viewModelScope.launch { _events.emit(PlaceDetailEvent.NavigateBack) }
    }
}

sealed interface PlaceDetailEvent {
    data object NavigateBack : PlaceDetailEvent
}
