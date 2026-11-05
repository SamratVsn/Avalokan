package com.example.avalokan.ui.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.local.PreferencesStore
import com.example.avalokan.domain.model.Place
import com.example.avalokan.domain.repository.PlaceRepository
import com.example.avalokan.ui.common.CONTENT_LOADING_TIMEOUT_MS
import com.example.avalokan.ui.navigation.NavDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Backs the "View All" page for a single place category.
 *
 * The category comes from the navigation argument, so the same screen serves
 * Historical, Cultural and any future category without hardcoding content.
 * Only places whose category matches are exposed to the UI.
 */
@HiltViewModel
class CategoryPlacesViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val userPreferences: PreferencesStore,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val category: String =
        savedStateHandle.get<String>(NavDestination.CategoryPlaces.ARG).orEmpty()

    private val allPlaces: StateFlow<List<Place>> = placeRepository.observePlaces()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val places: StateFlow<List<Place>> = allPlaces
        .map { places ->
            places.filter {
                category.isNotBlank() && it.category.equals(category, ignoreCase = true)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Total loaded places, so "category is empty" can be told apart from "load failed". */
    val totalPlaces: StateFlow<Int> = allPlaces
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val savedIds: StateFlow<Set<String>> = userPreferences.savedPlaceIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        viewModelScope.launch {
            // Any cached place means the filter can be evaluated, even when
            // this particular category has no entries.
            allPlaces.collect { places ->
                if (places.isNotEmpty()) _isLoading.value = false
            }
        }
        viewModelScope.launch {
            delay(CONTENT_LOADING_TIMEOUT_MS)
            _isLoading.value = false
        }
    }

    fun refresh() {
        if (_isRefreshing.value) return
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                placeRepository.refresh()
            } catch (_: Exception) {
                // Snapshot listeners keep streaming; the retry just ends.
            } finally {
                _isRefreshing.value = false
                _isLoading.value = false
            }
        }
    }

    fun toggleSave(placeId: String) {
        val current = savedIds.value.toMutableSet()
        if (!current.add(placeId)) current.remove(placeId)
        viewModelScope.launch { userPreferences.setSavedPlaceIds(current) }
    }
}
