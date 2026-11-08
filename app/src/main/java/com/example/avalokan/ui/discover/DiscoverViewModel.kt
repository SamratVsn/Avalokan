package com.example.avalokan.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.local.PreferencesStore
import com.example.avalokan.domain.classification.HeritageGroup
import com.example.avalokan.domain.model.Place
import com.example.avalokan.domain.repository.PlaceRepository
import com.example.avalokan.ui.common.ContentState
import com.example.avalokan.ui.common.asListContentState
import com.example.avalokan.ui.common.dataOrNull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

val DiscoverCategories: List<String> =
    listOf("All") + HeritageGroup.entries.map { it.key }


@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val userPreferences: PreferencesStore
) : ViewModel() {
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedCategory = MutableStateFlow(0)
    val selectedCategory: StateFlow<Int> = _selectedCategory.asStateFlow()

    /**
     * Bumping this restarts the repository stream. The initial value starts the
     * normal subscription; retry uses it to recover from a failed first load.
     */
    private val reload = MutableStateFlow(0)

    /**
     * The unfiltered repository state. This is what lets the UI tell "nothing
     * loaded at all" apart from "this filter has no matches".
     */
    val contentState: StateFlow<ContentState<List<Place>>> = reload
        .flatMapLatest { placeRepository.observePlaces().asListContentState() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            ContentState.Loading
        )

    val savedIds: StateFlow<Set<String>> = userPreferences.savedPlaceIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    /**
     * Single derived subscription shared by the filter-dependent flows, so the
     * repository is only observed once.
     */
    private data class DiscoverState(
        val sites: List<Place> = emptyList(),
        // Matches the chips' first paint, so the initial subscription does not
        // briefly emit an empty category row before the cache resolves.
        val availableCategories: List<String> = listOf("All"),
        val recommended: List<Place> = emptyList()
    )

    private val discoverState: StateFlow<DiscoverState> = combine(
        contentState,
        _query,
        _selectedCategory
    ) { content, query, selected ->
        val remote = content.dataOrNull.orEmpty()
        val categoryKey = DiscoverCategories.getOrElse(selected) { "All" }
        val typologyGroup = HeritageGroup.fromKey(categoryKey)
        DiscoverState(
            sites = remote.filter { site ->
                site.matches(typologyGroup) && site.matchesQuery(query)
            },
            availableCategories = DiscoverCategories.filter { filter ->
                val group = HeritageGroup.fromKey(filter)
                filter == "All" || remote.any { it.matches(group) }
            },
            // Editorial order: featured content first, then the repository's
            // curated order. Never rank places by an unsourced rating.
            recommended = remote
                .sortedByDescending { it.featured }
                .take(3)
        )
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DiscoverState())

    val sites: StateFlow<List<Place>> = discoverState
        .map { it.sites }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Categories that actually have at least one place — safe to suggest. */
    val availableCategories: StateFlow<List<String>> = discoverState
        .map { it.availableCategories }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            listOf("All")
        )

    /** A few places worth showing when a search returns nothing. */
    val recommended: StateFlow<List<Place>> = discoverState
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
            }
        }
    }

    /** Restarts the repository stream and forces a server read. */
    fun retry() {
        reload.value += 1
        refresh()
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
