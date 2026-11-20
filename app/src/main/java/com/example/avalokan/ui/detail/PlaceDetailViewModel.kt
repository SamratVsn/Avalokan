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

package com.example.avalokan.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.local.PreferencesStore
import com.example.avalokan.domain.model.Place
import com.example.avalokan.domain.model.Story
import com.example.avalokan.domain.repository.PlaceRepository
import com.example.avalokan.domain.repository.StoryRepository
import com.example.avalokan.ui.common.ContentState
import com.example.avalokan.ui.common.asItemContentState
import com.example.avalokan.ui.navigation.NavDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Connected content resolved for one place: explicit links only. */
data class PlaceRelatedContent(
    val places: List<Place> = emptyList(),
    val stories: List<Story> = emptyList()
)

@HiltViewModel
class PlaceDetailViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val storyRepository: StoryRepository,
    private val userPreferences: PreferencesStore,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val placeId: String =
        savedStateHandle.get<String>(NavDestination.PlaceDetail.ARG).orEmpty()

    private val reload = MutableStateFlow(0)

    val placeState: StateFlow<ContentState<Place>> = reload
        .flatMapLatest { placeRepository.observePlace(placeId).asItemContentState() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            ContentState.Loading
        )

    val savedIds: StateFlow<Set<String>> = userPreferences.savedPlaceIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    val visitedIds: StateFlow<Set<String>> = userPreferences.visitedPlaceIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    /**
     * Related content comes from recorded links only: `relatedPlaceIds` on the
     * place and `placeIds` on stories. No proximity or similarity is inferred,
     * so an empty result is the honest answer when nothing is linked.
     *
     * A failure in either stream must not take the whole detail page down, so
     * each side degrades to an empty list.
     */
    val relatedContent: StateFlow<PlaceRelatedContent> = combine(
        placeRepository.observePlaces().catch { emit(emptyList()) },
        storyRepository.observeStories().catch { emit(emptyList()) }
    ) { places, stories ->
        val place = places.firstOrNull { it.id == placeId }
        val relatedIds = place?.relatedPlaceIds.orEmpty()
            .filter { it.isNotBlank() && it != placeId }
        PlaceRelatedContent(
            places = relatedIds.mapNotNull { id -> places.firstOrNull { it.id == id } },
            stories = stories.filter { placeId in it.placeIds }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        PlaceRelatedContent()
    )

    private val _events = MutableSharedFlow<PlaceDetailEvent>()
    val events: SharedFlow<PlaceDetailEvent> = _events.asSharedFlow()

    /** Toggles save; returns true when the place is now saved. */
    fun toggleSave(): Boolean {
        val current = savedIds.value.toMutableSet()
        val nowSaved = if (!current.add(placeId)) {
            current.remove(placeId)
            false
        } else true
        viewModelScope.launch { userPreferences.setSavedPlaceIds(current) }
        return nowSaved
    }

    /**
     * Explicit visit toggle. This is the only writer to the visited set, so a
     * detail-screen view can never be mistaken for an actual visit.
     */
    fun toggleVisited(): Boolean {
        val current = visitedIds.value.toMutableSet()
        val nowVisited = if (!current.add(placeId)) {
            current.remove(placeId)
            false
        } else true
        viewModelScope.launch { userPreferences.setVisitedPlaceIds(current) }
        return nowVisited
    }

    /** Restarts the item stream and forces a server read. */
    fun retry() {
        reload.value += 1
        viewModelScope.launch {
            try {
                placeRepository.refresh()
            } catch (_: Exception) {
                // Snapshot listeners keep streaming; the retry just ends.
            }
        }
    }

    fun onBackClick() {
        viewModelScope.launch { _events.emit(PlaceDetailEvent.NavigateBack) }
    }
}

sealed interface PlaceDetailEvent {
    data object NavigateBack : PlaceDetailEvent
}
