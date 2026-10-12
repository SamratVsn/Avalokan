package com.example.avalokan.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.place.PlaceItem
import com.example.avalokan.data.place.PlaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    placeRepository: PlaceRepository
) : ViewModel() {
    /** Curated hero story — stays hardcoded by design, only its ID matters. */
    val heroStoryId: String = "boudhanath"

    val places: StateFlow<List<PlaceItem>> = placeRepository.observePlaces()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
