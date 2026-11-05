package com.example.avalokan

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.avalokan.ui.category.CategoryPlacesViewModel
import com.example.avalokan.ui.navigation.NavDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

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

    private fun viewModel(category: String) = CategoryPlacesViewModel(
        FakePlaceRepository(),
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
    fun `an empty category exposes nothing but still reports the loaded total`() = runTest {
        val vm = viewModel("Adventure")
        vm.totalPlaces.test {
            skipItems(1)
            assertEquals(2, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        // Nothing matched, yet places did load, so the screen must show the
        // "nothing in this category" state instead of a load failure.
        assertFalse(vm.isLoading.value)
        assertEquals(emptyList<String>(), vm.places.value.map { it.id })
    }

    @Test
    fun `loading stops once the first non-empty emission arrives`() = runTest {
        val vm = viewModel("Historical")
        vm.places.test {
            skipItems(1)
            awaitItem()
            assertFalse(vm.isLoading.value)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
