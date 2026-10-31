package com.example.avalokan.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.local.PreferencesStore
import com.example.avalokan.data.place.PlaceItem
import com.example.avalokan.data.place.PlaceRepository
import com.example.avalokan.ui.common.CONTENT_LOADING_TIMEOUT_MS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

val DiscoverCategories = listOf(
    "All",
    "Historical",
    "Spiritual",
    "Cultural",
    "Nature",
    "Adventure",
)


@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val userPreferences: PreferencesStore
) : ViewModel() {
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedCategory = MutableStateFlow(0)
    val selectedCategory: StateFlow<Int> = _selectedCategory.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            // A populated Room cache resolves this almost immediately. The
            // timeout only covers a cold, empty, offline start where the
            // repository may never emit.
            delay(CONTENT_LOADING_TIMEOUT_MS)
            _isLoading.value = false
        }
    }

    val savedIds: StateFlow<Set<String>> = userPreferences.savedPlaceIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    /**
     * Single upstream subscription shared by every derived flow, so the
     * repository is only observed once.
     *
     * [DiscoverState.total] is the number of places actually loaded, before
     * category/query filtering — it lets the UI tell "nothing matched this
     * filter" apart from "nothing loaded at all".
     */
    private data class DiscoverState(
        val sites: List<PlaceItem> = emptyList(),
        val total: Int = 0,
        val availableCategories: List<String> = emptyList(),
        val recommended: List<PlaceItem> = emptyList()
    )

    private val discoverState: StateFlow<DiscoverState> = combine(
        placeRepository.observePlaces(),
        _query,
        _selectedCategory
    ) { remote, query, selected ->
        val category = DiscoverCategories.getOrElse(selected) { "All" }
        DiscoverState(
            sites = remote.filter { site ->
                (category == "All" || site.category.equals(category, ignoreCase = true)) &&
                        (query.isBlank() || site.name.contains(query.trim(), ignoreCase = true))
            },
            total = remote.size,
            availableCategories = DiscoverCategories.filter { filter ->
                filter == "All" || remote.any { it.category.equals(filter, ignoreCase = true) }
            },
            recommended = remote
                .sortedWith(
                    compareByDescending<PlaceItem> { it.featured }
                        .thenByDescending { it.rating.toDoubleOrNull() ?: 0.0 }
                )
                .take(3)
        )
    }
        .onEach { state ->
            if (state.total > 0) _isLoading.value = false
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DiscoverState())

    val sites: StateFlow<List<PlaceItem>> = discoverState
        .map { it.sites }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val totalPlaces: StateFlow<Int> = discoverState
        .map { it.total }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    /** Categories that actually have at least one place — safe to suggest. */
    val availableCategories: StateFlow<List<String>> = discoverState
        .map { it.availableCategories }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            listOf("All")
        )

    /** A few places worth showing when a search returns nothing. */
    val recommended: StateFlow<List<PlaceItem>> = discoverState
        .map { it.recommended }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onQueryChange(value: String) { _query.value = value }

    fun onCategorySelect(index: Int) { _selectedCategory.value = index }

    /** Clears the search and jumps straight to a category that has content. */
    fun onSuggestionSelect(label: String) {
        _query.value = ""
        val index = DiscoverCategories.indexOfFirst { it.equals(label, ignoreCase = true) }
        if (index >= 0) _selectedCategory.value = index
    }

    fun clearQuery() {
        _query.value = ""
    }

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
                _isLoading.value = false
            }
        }
    }

    fun toggleSave(siteId: String): Boolean {
        val current = savedIds.value.toMutableSet()
        val nowSaved = if (!current.add(siteId)) {
            current.remove(siteId)
            false
        } else true
        viewModelScope.launch { userPreferences.setSavedPlaceIds(current) }
        return nowSaved
    }
}
