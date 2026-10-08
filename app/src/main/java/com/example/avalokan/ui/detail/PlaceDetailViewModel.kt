package com.example.avalokan.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.place.PlaceItem
import com.example.avalokan.data.place.PlaceRepository
import com.example.avalokan.ui.navigation.NavDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PlaceDetailViewModel @Inject constructor(
    placeRepository: PlaceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val placeId: String =
        savedStateHandle.get<String>(NavDestination.PlaceDetail.ARG).orEmpty()

    val place: StateFlow<PlaceItem?> = placeRepository.observePlace(placeId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
