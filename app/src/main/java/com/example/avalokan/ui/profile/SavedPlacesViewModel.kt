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

package com.example.avalokan.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.local.PreferencesStore
import com.example.avalokan.domain.model.Place
import com.example.avalokan.domain.repository.PlaceRepository
import com.example.avalokan.ui.common.ContentState
import com.example.avalokan.ui.common.asListContentState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Backs the full "Saved places" list reached from Profile's "See all".
 *
 * Every saved id that still resolves to a place is shown, most recently saved
 * first. Ids whose place no longer exists in the catalogue are not shown and
 * not counted, so this list and Profile's "Saved" stat always agree.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SavedPlacesViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val userPreferences: PreferencesStore,
) : ViewModel() {

    private val reload = MutableStateFlow(0)

    val savedIds: StateFlow<Set<String>> = userPreferences.savedPlaceIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    val state: StateFlow<ContentState<List<Place>>> = combine(
        reload.flatMapLatest { placeRepository.observePlaces().asListContentState() },
        userPreferences.savedPlaceIds,
        ::resolveSaved
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ContentState.Loading)

    private fun resolveSaved(
        catalogue: ContentState<List<Place>>,
        ids: Set<String>,
    ): ContentState<List<Place>> {
        // Nothing saved is a definite answer; no need to wait for the network.
        if (ids.isEmpty()) return ContentState.Empty
        return when (catalogue) {
            is ContentState.Content -> {
                val byId = catalogue.data.associateBy { it.id }
                val saved = ids.reversed().mapNotNull { byId[it] }
                if (saved.isEmpty()) ContentState.Empty else ContentState.Content(saved)
            }
            ContentState.Empty -> ContentState.Empty
            ContentState.Loading -> ContentState.Loading
            is ContentState.Error -> catalogue
        }
    }

    fun retry() {
        reload.value += 1
        viewModelScope.launch {
            try {
                placeRepository.refresh()
            } catch (_: Exception) {
                // The stream restart above surfaces any remaining failure.
            }
        }
    }

    /** Removes [placeId] and returns the previous set so the removal can be undone. */
    fun remove(placeId: String): Set<String> {
        val previous = savedIds.value
        viewModelScope.launch { userPreferences.setSavedPlaceIds(previous - placeId) }
        return previous
    }

    /** Undo for [remove]: restores the exact set, keeping the original save order. */
    fun restore(ids: Set<String>) {
        viewModelScope.launch { userPreferences.setSavedPlaceIds(ids) }
    }
}
