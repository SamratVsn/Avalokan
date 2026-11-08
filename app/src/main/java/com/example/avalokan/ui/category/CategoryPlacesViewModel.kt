package com.example.avalokan.ui.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.local.PreferencesStore
import com.example.avalokan.domain.model.Place
import com.example.avalokan.domain.repository.PlaceRepository
import com.example.avalokan.ui.common.ContentState
import com.example.avalokan.ui.common.asListContentState
import com.example.avalokan.ui.common.dataOrNull
import com.example.avalokan.ui.navigation.NavDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Backs the "View All" page for a single place category.
 *
 * The category comes from the navigation argument, so the same screen serves
 * Historical, Cultural and any future category without hardcoding content.
 * Only places whose category matches are exposed to the UI, while
 * [contentState] keeps the unfiltered repository state so the screen can tell
 * "this category is empty" apart from "nothing loaded at all".
 */
@HiltViewModel
class CategoryPlacesViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val userPreferences: PreferencesStore,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val category: String =
        savedStateHandle.get<String>(NavDestination.CategoryPlaces.ARG).orEmpty()

    /**
     * Bumping this restarts the repository stream. The initial value starts the
     * normal subscription; retry uses it to recover from a failed first load.
     */
    private val reload = MutableStateFlow(0)

    val contentState: StateFlow<ContentState<List<Place>>> = reload
        .flatMapLatest { placeRepository.observePlaces().asListContentState() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            ContentState.Loading
        )

    val places: StateFlow<List<Place>> = contentState
        .map { state ->
            state.dataOrNull.orEmpty().filter {
                category.isNotBlank() && it.category.equals(category, ignoreCase = true)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val savedIds: StateFlow<Set<String>> = userPreferences.savedPlaceIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

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
            }
        }
    }

    /** Restarts the repository stream and forces a server read. */
    fun retry() {
        reload.value += 1
        refresh()
    }

    fun toggleSave(placeId: String) {
        val current = savedIds.value.toMutableSet()
        if (!current.add(placeId)) current.remove(placeId)
        viewModelScope.launch { userPreferences.setSavedPlaceIds(current) }
    }
}
