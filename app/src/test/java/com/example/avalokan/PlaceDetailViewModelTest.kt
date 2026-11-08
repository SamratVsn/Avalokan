package com.example.avalokan

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
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
    ) = PlaceDetailViewModel(
        repository,
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
}
