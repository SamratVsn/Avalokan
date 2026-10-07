package com.example.avalokan.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.place.PlaceItem
import com.example.avalokan.data.place.PlaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

val DiscoverCategories = listOf("All", "Historical", "Cultural", "Nature")

/** Hardcoded fallback so the UI stays intact until Firestore has documents. */
private val FallbackSites = listOf(
    PlaceItem(
        id = "kathmandu-durbar",
        name = "Kathmandu Durbar Square",
        description = "The heart of old Kathmandu city, once the residence of the Nepalese Royal Family and home to the living goddess, Kumari.",
        meta = "Kathmandu • Historical Site",
        category = "Historical",
        rating = "4.8"
    ),
    PlaceItem(
        id = "lumbini-garden",
        name = "Lumbini Garden",
        description = "The sacred birthplace of Lord Buddha, a UNESCO World Heritage site offering profound peace and historical depth.",
        meta = "Lumbini • Spiritual Site",
        category = "Cultural",
        rating = "4.9"
    )
)

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    placeRepository: PlaceRepository
) : ViewModel() {
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedCategory = MutableStateFlow(0)
    val selectedCategory: StateFlow<Int> = _selectedCategory.asStateFlow()

    private val _savedIds = MutableStateFlow<Set<String>>(emptySet())
    val savedIds: StateFlow<Set<String>> = _savedIds.asStateFlow()

    val sites: StateFlow<List<PlaceItem>> = combine(
        placeRepository.observePlaces(),
        _query,
        _selectedCategory
    ) { remote, query, selected ->
        val all = remote.ifEmpty { FallbackSites }
        val category = DiscoverCategories.getOrElse(selected) { "All" }
        all.filter { site ->
            (category == "All" || site.category == category) &&
                    (query.isBlank() || site.name.contains(query, ignoreCase = true))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FallbackSites)

    fun onQueryChange(value: String) { _query.value = value }

    fun onCategorySelect(index: Int) { _selectedCategory.value = index }

    fun toggleSave(siteId: String): Boolean {
        val current = _savedIds.value.toMutableSet()
        val nowSaved = if (!current.add(siteId)) {
            current.remove(siteId)
            false
        } else true
        _savedIds.value = current
        return nowSaved
    }
}