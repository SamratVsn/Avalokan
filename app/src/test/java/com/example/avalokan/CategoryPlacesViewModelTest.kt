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
import com.example.avalokan.domain.model.PlaceSort
import com.example.avalokan.ui.category.CategoryPlacesViewModel
import com.example.avalokan.ui.common.ContentState
import com.example.avalokan.ui.navigation.NavDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class CategoryPlacesViewModelTest {

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
        category: String,
        repository: FakePlaceRepository = FakePlaceRepository()
    ) = CategoryPlacesViewModel(
        repository,
        FakePreferencesStore(),
        SavedStateHandle(mapOf(NavDestination.CategoryPlaces.ARG to category))
    )

    @Test
    fun `only places in the requested category are exposed`() = runTest {
        val vm = viewModel("Historical")
        vm.places.test {
            skipItems(1) // stateIn replays its empty initial value first
            assertEquals(listOf("kathmandu-durbar"), awaitItem().map { it.id })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `category matching is case insensitive`() = runTest {
        val vm = viewModel("historical")
        vm.places.test {
            skipItems(1)
            assertEquals(listOf("kathmandu-durbar"), awaitItem().map { it.id })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a different category returns only its own places`() = runTest {
        val vm = viewModel("Cultural")
        vm.places.test {
            skipItems(1)
            assertEquals(listOf("lumbini-garden"), awaitItem().map { it.id })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `an empty category exposes nothing but still reports content`() = runTest {
        val vm = viewModel("Adventure")
        vm.contentState.test {
            skipItems(1) // loading
            assertEquals(ContentState.Content(TestPlaces), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        // Nothing matched this category, yet places did load, so the screen
        // must show the "nothing in this category" state instead of a failure.
        assertEquals(emptyList<String>(), vm.places.value.map { it.id })
    }

    @Test
    fun `content state is loading until the first repository emission`() = runTest {
        val vm = viewModel("Historical")
        vm.contentState.test {
            assertEquals(ContentState.Loading, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `content state becomes empty when the repository answers with nothing`() = runTest {
        val vm = viewModel("Historical", FakePlaceRepository(emptyList()))
        vm.contentState.test {
            assertEquals(ContentState.Loading, awaitItem())
            advanceUntilIdle()
            assertEquals(ContentState.Empty, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `content state reports an error when the repository stream fails`() = runTest {
        val repository = FakePlaceRepository(
            placesFlow = flow { throw IOException("offline") }
        )
        val vm = viewModel("Historical", repository)
        vm.contentState.test {
            assertEquals(ContentState.Loading, awaitItem())
            advanceUntilIdle()
            assertTrue(awaitItem() is ContentState.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sort only reorders the category's own places`() = runTest {
        val places = listOf(
            Place(
                id = "zeta",
                name = "Zeta Temple",
                description = "",
                category = "Historical",
                district = "Bhaktapur"
            ),
            Place(
                id = "alpha",
                name = "Alpha Palace",
                description = "",
                category = "Historical",
                district = "Kathmandu"
            ),
            Place(
                id = "cultural",
                name = "Cultural Place",
                description = "",
                category = "Cultural"
            )
        )
        val vm = viewModel("Historical", FakePlaceRepository(places))
        vm.places.test {
            skipItems(1)
            assertEquals(listOf("zeta", "alpha"), awaitItem().map { it.id })
            vm.onSortChange(PlaceSort.NAME)
            assertEquals(listOf("alpha", "zeta"), awaitItem().map { it.id })
            vm.onSortChange(PlaceSort.REGION)
            assertEquals(listOf("zeta", "alpha"), awaitItem().map { it.id })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sort survives a new view model sharing the same saved state`() = runTest {
        val handle = SavedStateHandle(
            mapOf(NavDestination.CategoryPlaces.ARG to "Historical")
        )
        val first = CategoryPlacesViewModel(
            FakePlaceRepository(),
            FakePreferencesStore(),
            handle
        )
        first.onSortChange(PlaceSort.NAME)

        val restored = CategoryPlacesViewModel(
            FakePlaceRepository(),
            FakePreferencesStore(),
            handle
        )
        assertEquals(PlaceSort.NAME, restored.sort.value)
    }
}
