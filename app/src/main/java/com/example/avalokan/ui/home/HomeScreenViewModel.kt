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

package com.example.avalokan.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.domain.model.Place
import com.example.avalokan.domain.model.Story
import com.example.avalokan.domain.repository.PlaceRepository
import com.example.avalokan.domain.repository.StoryRepository
import com.example.avalokan.ui.common.ContentState
import com.example.avalokan.ui.common.asListContentState
import com.example.avalokan.ui.common.asItemContentState
import com.example.avalokan.ui.common.dataOrNull
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

/** A Home entry point into one of the place categories that has content. */
data class HomeCategory(val name: String, val count: Int)

/** Preferred reading order; unknown categories follow, most populated first. */
private val CategoryDisplayOrder =
    listOf("historical", "cultural", "spiritual", "nature", "adventure")

/**
 * Canonical spelling for the categories this build names. CategoryPlaces
 * matches case-insensitively, so normalising here keeps Home's labels and keys
 * stable even when documents were entered with different casing.
 */
private val CanonicalCategoryNames = mapOf(
    "historical" to "Historical",
    "cultural" to "Cultural",
    "spiritual" to "Spiritual",
    "nature" to "Nature",
    "adventure" to "Adventure"
)

/**
 * Groups places by their stored `category` value. Known categories use their
 * canonical spelling; unknown values keep the stored spelling, so a curator's
 * new category still gets an entry point. CategoryPlaces matches
 * case-insensitively, so either form opens the right page.
 */
internal fun List<Place>.categorySummaries(): List<HomeCategory> {
    val counts = linkedMapOf<String, Int>()
    val displayNames = mutableMapOf<String, String>()
    for (place in this) {
        val stored = place.category.trim()
        if (stored.isEmpty()) continue
        val key = stored.lowercase()
        val name = CanonicalCategoryNames[key] ?: displayNames.getOrPut(key) { stored }
        counts[name] = (counts[name] ?: 0) + 1
    }
    return counts.map { (name, count) -> HomeCategory(name, count) }
        .sortedWith(
            compareBy<HomeCategory>(
                { category ->
                    CategoryDisplayOrder.indexOf(category.name.lowercase())
                        .takeIf { it >= 0 } ?: Int.MAX_VALUE
                },
                { category -> -category.count },
                { category -> category.name.lowercase() }
            )
        )
}

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {
    /**
     * Editorial pin for Story of the Day. Home prefers this curated document but
     * falls back to the newest available story, so deleting or renaming the
     * document can never leave the hero blank.
     */
    private val curatedHeroStoryId: String = "boudhanath"

    /**
     * Bumping this restarts both content streams. The initial value starts the
     * normal subscription; retry uses it to pick the repository back up after
     * a failed first load.
     */
    private val reload = MutableStateFlow(0)

    /** Hero = whatever the stories collection holds (curated doc preferred). */
    val heroStoryState: StateFlow<ContentState<Story>> = reload
        .flatMapLatest {
            storyRepository.observeStories()
                .map { stories ->
                    stories.find { it.id == curatedHeroStoryId } ?: stories.firstOrNull()
                }
                .asItemContentState()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            ContentState.Loading
        )

    val placesState: StateFlow<ContentState<List<Place>>> = reload
        .flatMapLatest { placeRepository.observePlaces().asListContentState() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            ContentState.Loading
        )

    /** Categories that actually contain places, with their counts. */
    val categories: StateFlow<List<HomeCategory>> = placesState
        .map { it.dataOrNull.orEmpty().categorySummaries() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun refresh() {
        if (_isRefreshing.value) return
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                placeRepository.refresh()
                storyRepository.refresh()
            } catch (_: Exception) {
                // Snapshot listeners keep streaming; swipe just ends.
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    /** Restarts the streams and forces a server read after a failed load. */
    fun retry() {
        reload.value += 1
        refresh()
    }
}
