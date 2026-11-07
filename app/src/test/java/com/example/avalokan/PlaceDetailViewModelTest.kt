package com.example.avalokan

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.avalokan.ui.detail.PlaceDetailViewModel
import com.example.avalokan.ui.navigation.NavDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

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
    ) = PlaceDetailViewModel(
        FakePlaceRepository(),
        prefs,
        SavedStateHandle(mapOf(NavDestination.PlaceDetail.ARG to placeId)),
    )

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
