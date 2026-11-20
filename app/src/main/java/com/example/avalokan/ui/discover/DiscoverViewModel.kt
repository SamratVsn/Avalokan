/*
 * Copyright (C) 2026 Samrat Parajuli
 *
 * This file is part of Avalokan.
 *
 * Avalokan is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * Avalokan is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Avalokan. If not, see <https://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */

package com.example.avalokan.ui.discover

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.local.PreferencesStore
import com.example.avalokan.domain.classification.HeritageGroup
import com.example.avalokan.domain.model.Place
import com.example.avalokan.domain.model.PlaceSort
import com.example.avalokan.domain.model.sortedFor
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
    private val userPreferences: PreferencesStore,
    private val savedStateHandle: SavedStateHandle = SavedStateHandle()
) : ViewModel() {
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedCategory = MutableStateFlow(0)
    val selectedCategory: StateFlow<Int> = _selectedCategory.asStateFlow()

    /**
     * Comparison order, kept in [SavedStateHandle] so it survives configuration
     * changes and process death. "Recommended" is always available as a reset.
     */
    private val _sort = MutableStateFlow(
        PlaceSort.fromName(savedStateHandle.get<String>(SORT_KEY))
    )
    val sort: StateFlow<PlaceSort> = _sort.asStateFlow()

    fun onSortChange(value: PlaceSort) {
        _sort.value = value
        savedStateHandle[SORT_KEY] = value.name
    }

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
        _selectedCategory,
        _sort
    ) { content, query, selected, sort ->
        val remote = content.dataOrNull.orEmpty()
        val categoryKey = DiscoverCategories.getOrElse(selected) { "All" }
        val typologyGroup = HeritageGroup.fromKey(categoryKey)
        DiscoverState(
            // Search relevance decides membership order first; the chosen sort
            // then reorders the matches. sortedBy is stable, so curated order is
            // kept within each relevance tier when "Recommended" is active.
            sites = remote
                .filter { site -> site.matches(typologyGroup) }
                .mapNotNull { site -> site.searchRank(query)?.let { rank -> site to rank } }
                .sortedBy { (_, rank) -> rank }
                .map { (site, _) -> site }
                .sortedFor(sort),
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

    private companion object {
        const val SORT_KEY = "discover_sort"
    }
}
