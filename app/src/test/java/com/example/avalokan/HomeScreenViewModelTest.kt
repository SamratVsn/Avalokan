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

import app.cash.turbine.test
import com.example.avalokan.ui.common.ContentState
import com.example.avalokan.ui.home.HomeCategory
import com.example.avalokan.ui.home.HomeScreenViewModel
import com.example.avalokan.ui.home.categorySummaries
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
class HomeScreenViewModelTest {

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
        places: FakePlaceRepository = FakePlaceRepository(),
        stories: FakeStoryRepository = FakeStoryRepository()
    ) = HomeScreenViewModel(places, stories)

    @Test
    fun `places begin loading and become content`() = runTest {
        val vm = viewModel()
        vm.placesState.test {
            assertEquals(ContentState.Loading, awaitItem())
            assertEquals(ContentState.Content(TestPlaces), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `places become empty when the repository answers with nothing`() = runTest {
        val vm = viewModel(places = FakePlaceRepository(emptyList()))
        vm.placesState.test {
            assertEquals(ContentState.Loading, awaitItem())
            advanceUntilIdle()
            assertEquals(ContentState.Empty, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `places report an error when the repository stream fails`() = runTest {
        val vm = viewModel(
            places = FakePlaceRepository(placesFlow = flow { throw IOException("offline") })
        )
        vm.placesState.test {
            assertEquals(ContentState.Loading, awaitItem())
            advanceUntilIdle()
            assertTrue(awaitItem() is ContentState.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `hero story begins loading and becomes content`() = runTest {
        val vm = viewModel()
        vm.heroStoryState.test {
            assertEquals(ContentState.Loading, awaitItem())
            assertEquals(ContentState.Content(TestStories.first()), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `hero story becomes empty only after the load window`() = runTest {
        val vm = viewModel(stories = FakeStoryRepository(emptyList()))
        vm.heroStoryState.test {
            assertEquals(ContentState.Loading, awaitItem())
            // No empty copy may be produced while the story is still loading.
            expectNoEvents()
            advanceUntilIdle()
            assertEquals(ContentState.Empty, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- Explore by category ---------------------------------------------

    @Test
    fun `home categories group content by stored category`() = runTest {
        val vm = viewModel()
        vm.categories.test {
            assertEquals(emptyList<HomeCategory>(), awaitItem())
            advanceUntilIdle()
            assertEquals(
                listOf(HomeCategory("Historical", 1), HomeCategory("Cultural", 1)),
                awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `categories keep reading order and merge case variants`() {
        val base = TestPlaces.first()
        val places = listOf(
            base.copy(id = "1", category = "cultural"),
            base.copy(id = "2", category = "Wellness"),
            base.copy(id = "3", category = "Historical"),
            base.copy(id = "4", category = "Historical"),
            base.copy(id = "5", category = "Wellness"),
            base.copy(id = "6", category = "Wellness")
        )
        assertEquals(
            listOf(
                HomeCategory("Historical", 2),
                HomeCategory("Cultural", 1),
                HomeCategory("Wellness", 3)
            ),
            places.categorySummaries()
        )
    }

    @Test
    fun `places without a category are not offered as an entry point`() {
        val base = TestPlaces.first()
        val places = listOf(
            base.copy(id = "1", category = ""),
            base.copy(id = "2", category = "   ")
        )
        assertTrue(places.categorySummaries().isEmpty())
    }
}
