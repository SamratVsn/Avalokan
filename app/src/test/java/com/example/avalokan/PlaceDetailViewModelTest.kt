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

package com.example.avalokan

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.avalokan.domain.model.Place
import com.example.avalokan.domain.model.Story
import com.example.avalokan.ui.common.ContentState
import com.example.avalokan.ui.detail.PlaceDetailViewModel
import com.example.avalokan.ui.navigation.NavDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

/**
 * Visit tracking is explicit. Opening the detail screen must never create a
 * visit; only the "Mark as visited" action writes to the persisted set.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PlaceDetailViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(
        prefs: FakePreferencesStore,
        placeId: String = "kathmandu-durbar",
        repository: FakePlaceRepository = FakePlaceRepository(),
        stories: FakeStoryRepository = FakeStoryRepository(),
    ) = PlaceDetailViewModel(
        repository,
        stories,
        prefs,
        SavedStateHandle(mapOf(NavDestination.PlaceDetail.ARG to placeId)),
    )

    @Test
    fun `place begins loading and becomes content`() = runTest {
        val vm = viewModel(FakePreferencesStore())
        vm.placeState.test {
            assertEquals(ContentState.Loading, awaitItem())
            assertEquals(
                ContentState.Content(TestPlaces.first()),
                awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a missing place becomes empty after the load window`() = runTest {
        val vm = viewModel(FakePreferencesStore(), placeId = "does-not-exist")
        vm.placeState.test {
            assertEquals(ContentState.Loading, awaitItem())
            expectNoEvents()
            advanceUntilIdle()
            assertEquals(ContentState.Empty, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a failed place stream becomes an error`() = runTest {
        val vm = viewModel(
            FakePreferencesStore(),
            repository = FakePlaceRepository(
                placeFlow = flow { throw IOException("offline") }
            )
        )
        vm.placeState.test {
            assertEquals(ContentState.Loading, awaitItem())
            advanceUntilIdle()
            assertTrue(awaitItem() is ContentState.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `opening a place detail does not mark it visited`() = runTest {
        val prefs = FakePreferencesStore()
        viewModel(prefs)

        assertTrue(prefs.visitedPlaceIds.first().isEmpty())
    }

    @Test
    fun `marking a place visited persists and toggles back off`() = runTest {
        val prefs = FakePreferencesStore()
        val vm = viewModel(prefs)

        vm.visitedIds.test {
            skipItems(1) // stateIn initial empty set
            assertTrue(vm.toggleVisited())
            assertTrue(awaitItem().contains("kathmandu-durbar"))

            assertFalse(vm.toggleVisited())
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }

        assertTrue(prefs.visitedPlaceIds.first().isEmpty())
    }

    @Test
    fun `related places follow recorded ids and skip unresolvable ones`() = runTest {
        val host = Place(
            id = "kathmandu-durbar",
            name = "Kathmandu Durbar Square",
            description = "",
            relatedPlaceIds = listOf("patan-durbar", "missing", "kathmandu-durbar")
        )
        val patan = Place(id = "patan-durbar", name = "Patan Durbar Square", description = "")
        val vm = viewModel(
            FakePreferencesStore(),
            repository = FakePlaceRepository(listOf(host, patan))
        )

        vm.relatedContent.test {
            skipItems(1) // initial empty state
            val related = awaitItem()
            assertEquals(listOf("patan-durbar"), related.places.map { it.id })
            assertTrue(related.stories.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `only stories that reference this place are related`() = runTest {
        val host = Place(
            id = "kathmandu-durbar",
            name = "Kathmandu Durbar Square",
            description = ""
        )
        val linked = Story(
            id = "durbar-story",
            title = "The Living Square",
            placeIds = listOf("kathmandu-durbar")
        )
        val unrelated = Story(
            id = "other-story",
            title = "Elsewhere",
            placeIds = listOf("patan-durbar")
        )
        val vm = viewModel(
            FakePreferencesStore(),
            repository = FakePlaceRepository(listOf(host)),
            stories = FakeStoryRepository(listOf(linked, unrelated))
        )

        vm.relatedContent.test {
            skipItems(1)
            val related = awaitItem()
            assertEquals(listOf("durbar-story"), related.stories.map { it.id })
            assertTrue(related.places.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `no recorded links means no related content`() = runTest {
        val host = Place(
            id = "kathmandu-durbar",
            name = "Kathmandu Durbar Square",
            description = ""
        )
        val vm = viewModel(
            FakePreferencesStore(),
            repository = FakePlaceRepository(listOf(host)),
            stories = FakeStoryRepository(emptyList())
        )

        vm.relatedContent.test {
            val related = awaitItem()
            assertTrue(related.places.isEmpty())
            assertTrue(related.stories.isEmpty())
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
