package com.example.avalokan.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.local.PreferencesStore
import com.example.avalokan.domain.model.Place
import com.example.avalokan.domain.repository.PlaceRepository
import com.example.avalokan.ui.navigation.NavDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceDetailViewModel @Inject constructor(
    placeRepository: PlaceRepository,
    private val userPreferences: PreferencesStore,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val placeId: String =
        savedStateHandle.get<String>(NavDestination.PlaceDetail.ARG).orEmpty()

    val place: StateFlow<Place?> = placeRepository.observePlace(placeId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

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

    fun onBackClick() {
        viewModelScope.launch { _events.emit(PlaceDetailEvent.NavigateBack) }
    }
}

sealed interface PlaceDetailEvent {
    data object NavigateBack : PlaceDetailEvent
}
