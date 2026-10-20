package com.example.avalokan.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.auth.AuthRepository
import com.example.avalokan.data.auth.AuthState
import com.example.avalokan.data.event.EventItem
import com.example.avalokan.data.event.EventRepository
import com.example.avalokan.ui.navigation.NavDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    eventRepository: EventRepository,
    savedStateHandle: SavedStateHandle,
    authRepository: AuthRepository,
) : ViewModel() {
    private val eventId: String =
        savedStateHandle.get<String>(NavDestination.EventDetail.ARG).orEmpty()

    val event: StateFlow<EventItem?> = eventRepository.observeEvent(eventId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

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

    private val _events = MutableSharedFlow<EventDetailEvent>()
    val events: SharedFlow<EventDetailEvent> = _events.asSharedFlow()

    /** Attempts to join/book the event; prompts sign-in when not authed. */
    fun onBookEvent() {
        val currentAuth = authState.value
        if (!currentAuth.canSaveOrJoin) {
            viewModelScope.launch { _events.emit(EventDetailEvent.AuthRequired) }
            return
        }
        viewModelScope.launch { _events.emit(EventDetailEvent.Booked) }
    }

    fun onBackClick() {
        viewModelScope.launch { _events.emit(EventDetailEvent.NavigateBack) }
    }
}

sealed interface EventDetailEvent {
    data object NavigateBack : EventDetailEvent
    data object AuthRequired : EventDetailEvent
    data object Booked : EventDetailEvent
}
