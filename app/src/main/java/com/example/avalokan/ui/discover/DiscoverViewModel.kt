package com.example.avalokan.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.local.PreferencesStore
import com.example.avalokan.data.place.PlaceItem
import com.example.avalokan.data.place.PlaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

val DiscoverCategories = listOf(
    "All",
    "Historical",
    "Spiritual",
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

    val savedIds: StateFlow<Set<String>> = userPreferences.savedPlaceIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    val sites: StateFlow<List<PlaceItem>> = combine(
        placeRepository.observePlaces(),
        _query,
        _selectedCategory
    ) { remote, query, selected ->
        val category = DiscoverCategories.getOrElse(selected) { "All" }
        remote.filter { site ->
            (category == "All" || site.category == category) &&
                    (query.isBlank() || site.name.contains(query, ignoreCase = true))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onQueryChange(value: String) { _query.value = value }

    fun onCategorySelect(index: Int) { _selectedCategory.value = index }

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun refresh() {
        if (_isRefreshing.value) return
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                placeRepository.refresh()
            } catch (_: Exception) {
            } finally {
                _isRefreshing.value = false
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