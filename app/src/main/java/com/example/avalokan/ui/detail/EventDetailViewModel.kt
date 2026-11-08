package com.example.avalokan.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.local.PreferencesStore
import com.example.avalokan.domain.model.Event
import com.example.avalokan.domain.repository.EventRepository
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
class EventDetailViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val userPreferences: PreferencesStore,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val eventId: String =
        savedStateHandle.get<String>(NavDestination.EventDetail.ARG).orEmpty()

    private val reload = MutableStateFlow(0)

    val eventState: StateFlow<ContentState<Event>> = reload
        .flatMapLatest { eventRepository.observeEvent(eventId).asItemContentState() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            ContentState.Loading
        )

    val favoriteIds: StateFlow<Set<String>> = userPreferences.favoriteEventIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    private val _events = MutableSharedFlow<EventDetailEvent>()
    val events: SharedFlow<EventDetailEvent> = _events.asSharedFlow()

    fun toggleFavorite() {
        val current = favoriteIds.value.toMutableSet()
        if (!current.add(eventId)) current.remove(eventId)
        viewModelScope.launch { userPreferences.setFavoriteEventIds(current) }
    }

    /** Restarts the item stream and forces a server read. */
    fun retry() {
        reload.value += 1
        viewModelScope.launch {
            try {
                eventRepository.refresh()
            } catch (_: Exception) {
                // Snapshot listeners keep streaming; the retry just ends.
            }
        }
    }

    fun onBackClick() {
        viewModelScope.launch { _events.emit(EventDetailEvent.NavigateBack) }
    }
}

sealed interface EventDetailEvent {
    data object NavigateBack : EventDetailEvent
}
