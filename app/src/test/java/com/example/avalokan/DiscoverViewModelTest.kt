package com.example.avalokan

import app.cash.turbine.test
import com.example.avalokan.ui.discover.DiscoverCategories
import com.example.avalokan.ui.discover.DiscoverViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@OptIn(ExperimentalCoroutinesApi::class)
class DiscoverViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `category chip filters sites`() = runTest {
        val vm = DiscoverViewModel(FakePlaceRepository(), FakePreferencesStore())
        vm.sites.test {
            skipItems(1) // stateIn replays its empty initial value first
            assertEquals(2, awaitItem().size) // All
            vm.onCategorySelect(1) // Historical
            assertEquals(listOf("kathmandu-durbar"), awaitItem().map { it.id })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search query filters by name`() = runTest {
        val vm = DiscoverViewModel(FakePlaceRepository(), FakePreferencesStore())
        vm.sites.test {
            awaitItem()
            vm.onQueryChange("lumbini")
            assertEquals(listOf("lumbini-garden"), awaitItem().map { it.id })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggle save persists and flips`() = runTest {
        val vm = DiscoverViewModel(FakePlaceRepository(), FakePreferencesStore())
        assertTrue(vm.toggleSave("kathmandu-durbar"))
        advanceUntilIdle()
        vm.savedIds.test {
            skipItems(1) // stateIn replays its initial value first
            assertEquals(setOf("kathmandu-durbar"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `totalPlaces counts every place the repository loaded`() = runTest {
        val vm = DiscoverViewModel(FakePlaceRepository(), FakePreferencesStore())
        vm.totalPlaces.test {
            skipItems(1) // stateIn replays its zero initial value first
            assertEquals(2, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a filter with no matches empties sites but still counts loaded places`() = runTest {
        val vm = DiscoverViewModel(FakePlaceRepository(), FakePreferencesStore())
        vm.sites.test {
            skipItems(1)
            assertEquals(2, awaitItem().size) // All
            vm.onCategorySelect(DiscoverCategories.indexOf("Adventure"))
            assertEquals(emptyList<String>(), awaitItem().map { it.id })
            // Nothing matched, yet the repository did load both places — the
            // screen needs this to avoid claiming a connection failure.
            vm.totalPlaces.test {
                skipItems(1)
                assertEquals(2, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `totalPlaces does not shrink when a query filters everything out`() = runTest {
        val vm = DiscoverViewModel(FakePlaceRepository(), FakePreferencesStore())
        vm.totalPlaces.test {
            skipItems(1)
            assertEquals(2, awaitItem())
            vm.onQueryChange("no such place at all")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loading stops once the first non-empty emission arrives`() = runTest {
        val vm = DiscoverViewModel(FakePlaceRepository(), FakePreferencesStore())
        vm.sites.test {
            skipItems(1)
            awaitItem()
            assertFalse(vm.isLoading.value)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `suggestion select clears the query and switches category`() = runTest {
        val vm = DiscoverViewModel(FakePlaceRepository(), FakePreferencesStore())
        vm.onQueryChange("nothing matches this")
        vm.onSuggestionSelect("Historical")
        assertEquals("", vm.query.value)
        assertEquals(DiscoverCategories.indexOf("Historical"), vm.selectedCategory.value)
    }
}
