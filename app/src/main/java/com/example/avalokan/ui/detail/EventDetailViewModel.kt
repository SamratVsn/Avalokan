package com.example.avalokan.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.event.EventItem
import com.example.avalokan.data.event.EventRepository
import com.example.avalokan.ui.navigation.NavDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    eventRepository: EventRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val eventId: String =
        savedStateHandle.get<String>(NavDestination.EventDetail.ARG).orEmpty()

    val event: StateFlow<EventItem?> = eventRepository.observeEvent(eventId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
