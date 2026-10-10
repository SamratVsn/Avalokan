package com.example.avalokan.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.event.EventItem
import com.example.avalokan.data.event.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Hardcoded fallback so the UI stays intact until Firestore has documents. */
private val FallbackEvents = listOf(
    EventItem(
        id = "bhaktapur-pottery",
        title = "Bhaktapur Pottery Workshop",
        description = "Hands-on clay workshop in the old pottery square.",
        meta = "Bhaktapur Square • 10:00 AM",
        fee = "FREE ENTRY",
        action = "Join >",
        type = "CULTURAL"
    ),
    EventItem(
        id = "samay-baji",
        title = "Alla & Samay Baji Festival",
        description = "Newari feast and street celebration in Patan.",
        meta = "Patan Square • 5:00 PM",
        fee = "$15 ENTRY",
        action = "Sign Up >",
        type = "RELIGIOUS"
    ),
    EventItem(
        id = "thangka-demo",
        title = "Live Thangka Art Demo",
        description = "Watch master painters at work in Boudha.",
        meta = "Boudha • 11:00 AM",
        fee = "DONATION BASED",
        action = "More Info >",
        type = "CULTURAL"
    )
)

@HiltViewModel
class EventsViewModel @Inject constructor(
    eventRepository: EventRepository
) : ViewModel() {
    val events: StateFlow<List<EventItem>> = eventRepository.observeEvents()
        .map { remote -> remote.ifEmpty { FallbackEvents } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FallbackEvents)
}