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

package com.example.avalokan.ui.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.local.PreferencesStore
import com.example.avalokan.domain.model.Place
import com.example.avalokan.domain.model.PlaceSort
import com.example.avalokan.domain.model.sortedFor
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
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
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val category: String =
        savedStateHandle.get<String>(NavDestination.CategoryPlaces.ARG).orEmpty()

    /** Same comparison order as Discover; persisted while the entry survives. */
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

    val contentState: StateFlow<ContentState<List<Place>>> = reload
        .flatMapLatest { placeRepository.observePlaces().asListContentState() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            ContentState.Loading
        )

    val places: StateFlow<List<Place>> = combine(contentState, _sort) { state, sort ->
        state.dataOrNull.orEmpty()
            .filter {
                category.isNotBlank() && it.category.equals(category, ignoreCase = true)
            }
            .sortedFor(sort)
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

    private companion object {
        const val SORT_KEY = "category_places_sort"
    }
}
