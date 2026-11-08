package com.example.avalokan

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
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
}
