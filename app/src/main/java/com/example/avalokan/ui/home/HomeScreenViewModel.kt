package com.example.avalokan.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.place.PlaceItem
import com.example.avalokan.data.place.PlaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Hardcoded fallback so the UI stays intact until Firestore has documents. */
private val FallbackPlaces = listOf(
    PlaceItem(
        id = "patan-durbar",
        name = "Patan Durbar Square",
        description = "Artistic heritage of Lalitpur.",
        meta = "Lalitpur • Historical Site",
        category = "Historical",
        rating = "4.8"
    ),
    PlaceItem(
        id = "swayambhu",
        name = "Swayambhu",
        description = "The Ancient Hill.",
        meta = "Kathmandu • Spiritual Site",
        category = "Historical",
        rating = "4.7"
    ),
    PlaceItem(
        id = "boudha-stupa",
        name = "Boudha Stupa",
        description = "Boudhanath • Spiritual Site.",
        meta = "Boudhanath • Spiritual Site",
        category = "Cultural",
        rating = "4.9"
    ),
    PlaceItem(
        id = "bhaktapur-pottery",
        name = "Bhaktapur Pottery Square",
        description = "Bhaktapur • Craft Quarter.",
        meta = "Bhaktapur • Craft Quarter",
        category = "Cultural",
        rating = "4.7"
    )
)

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    placeRepository: PlaceRepository
) : ViewModel() {
    /** Curated hero story — stays hardcoded by design, only its ID matters. */
    val heroStoryId: String = "boudhanath"

    val places: StateFlow<List<PlaceItem>> = placeRepository.observePlaces()
        .map { remote -> remote.ifEmpty { FallbackPlaces } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FallbackPlaces)
}