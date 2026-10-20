package com.example.avalokan.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.auth.AuthRepository
import com.example.avalokan.data.auth.AuthState
import com.example.avalokan.data.local.PreferencesStore
import com.example.avalokan.data.place.PlaceItem
import com.example.avalokan.data.place.PlaceRepository
import com.example.avalokan.ui.navigation.NavDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceDetailViewModel @Inject constructor(
    placeRepository: PlaceRepository,
    private val userPreferences: PreferencesStore,
    savedStateHandle: SavedStateHandle,
    authRepository: AuthRepository,
) : ViewModel() {
    private val placeId: String =
        savedStateHandle.get<String>(NavDestination.PlaceDetail.ARG).orEmpty()

    val place: StateFlow<PlaceItem?> = placeRepository.observePlace(placeId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val savedIds: StateFlow<Set<String>> = userPreferences.savedPlaceIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    private val authState = authRepository.currentUser
        .map { user ->
            AuthState(
                isAuthenticated = user != null,
                isAnonymous = user?.isAnonymous == true,
                uid = user?.uid,
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            AuthState()
        )

    private val _events = kotlinx.coroutines.flow.MutableSharedFlow<PlaceDetailEvent>()
    val events: kotlinx.coroutines.flow.SharedFlow<PlaceDetailEvent> = _events.asSharedFlow()

    /** Toggles save; returns true when the place is now saved. */
    fun toggleSave(): Boolean {
        val currentAuth = authState.value
        if (!currentAuth.canSaveOrJoin) {
            viewModelScope.launch { _events.emit(PlaceDetailEvent.AuthRequired) }
            return false
        }
        val current = savedIds.value.toMutableSet()
        val nowSaved = if (!current.add(placeId)) {
            current.remove(placeId)
            false
        } else true
        viewModelScope.launch { userPreferences.setSavedPlaceIds(current) }
        return nowSaved
    }

    fun onBackClick() {
        viewModelScope.launch { _events.emit(PlaceDetailEvent.NavigateBack) }
    }
}

sealed interface PlaceDetailEvent {
    data object NavigateBack : PlaceDetailEvent
    data object AuthRequired : PlaceDetailEvent
}
