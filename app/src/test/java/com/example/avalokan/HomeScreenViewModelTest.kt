package com.example.avalokan

import app.cash.turbine.test
import com.example.avalokan.ui.common.ContentState
import com.example.avalokan.ui.home.HomeScreenViewModel
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
}
