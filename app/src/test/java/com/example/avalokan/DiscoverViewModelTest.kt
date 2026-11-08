package com.example.avalokan

import app.cash.turbine.test
import com.example.avalokan.domain.model.Place
import com.example.avalokan.ui.common.ContentState
import com.example.avalokan.ui.discover.DiscoverCategories
import com.example.avalokan.ui.discover.DiscoverViewModel
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

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
            vm.onCategorySelect(DiscoverCategories.indexOf("PAGODA_NEWA"))
            assertEquals(listOf("kathmandu-durbar"), awaitItem().map { it.id })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `place with multiple typologies appears once under each matching filter`() = runTest {
        val vm = DiscoverViewModel(FakePlaceRepository(), FakePreferencesStore())
        vm.sites.test {
            skipItems(1)
            awaitItem()
            vm.onCategorySelect(DiscoverCategories.indexOf("STUPA_MONASTERY"))
            assertEquals(listOf("lumbini-garden"), awaitItem().map { it.id })
            // Go through All so the next filter produces a distinct emission;
            // both STUPA_MONASTERY and SACRED_LANDSCAPE match Lumbini.
            vm.onCategorySelect(0)
            assertEquals(2, awaitItem().size)
            vm.onCategorySelect(DiscoverCategories.indexOf("SACRED_LANDSCAPE"))
            assertEquals(listOf("lumbini-garden"), awaitItem().map { it.id })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search composes with the selected heritage typology`() = runTest {
        val vm = DiscoverViewModel(FakePlaceRepository(), FakePreferencesStore())
        vm.sites.test {
            skipItems(1)
            awaitItem()
            vm.onCategorySelect(DiscoverCategories.indexOf("PAGODA_NEWA"))
            assertEquals(listOf("kathmandu-durbar"), awaitItem().map { it.id })
            vm.onQueryChange("lumbini")
            assertEquals(emptyList<String>(), awaitItem().map { it.id })
            vm.onCategorySelect(0)
            assertEquals(listOf("lumbini-garden"), awaitItem().map { it.id })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `legacy place without typologies is safe and appears only under All`() = runTest {
        val legacy = Place(
            id = "legacy-place",
            name = "Legacy Place",
            description = "desc",
            category = "Historical"
        )
        val vm = DiscoverViewModel(
            FakePlaceRepository(TestPlaces + legacy),
            FakePreferencesStore()
        )
        vm.sites.test {
            skipItems(1)
            assertEquals(3, awaitItem().size)
            vm.onCategorySelect(DiscoverCategories.indexOf("PAGODA_NEWA"))
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
    fun `content state counts every place the repository loaded`() = runTest {
        val vm = DiscoverViewModel(FakePlaceRepository(), FakePreferencesStore())
        vm.contentState.test {
            assertEquals(ContentState.Loading, awaitItem()) // initial stateIn value
            assertEquals(ContentState.Content(TestPlaces), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a filter with no matches empties sites but still reports content`() = runTest {
        val vm = DiscoverViewModel(FakePlaceRepository(), FakePreferencesStore())
        vm.sites.test {
            skipItems(1)
            assertEquals(2, awaitItem().size) // All
            vm.onCategorySelect(DiscoverCategories.indexOf("SACRED_WATER"))
            assertEquals(emptyList<String>(), awaitItem().map { it.id })
            // Nothing matched, yet the repository did load both places — the
            // screen needs this to avoid claiming a connection failure.
            assertEquals(ContentState.Content(TestPlaces), vm.contentState.value)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `content state does not shrink when a query filters everything out`() = runTest {
        val vm = DiscoverViewModel(FakePlaceRepository(), FakePreferencesStore())
        vm.contentState.test {
            assertEquals(ContentState.Loading, awaitItem())
            assertEquals(ContentState.Content(TestPlaces), awaitItem())
            vm.onQueryChange("no such place at all")
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `content state is loading, content, empty or error as the repository changes`() = runTest {
        val vm = DiscoverViewModel(FakePlaceRepository(), FakePreferencesStore())
        vm.contentState.test {
            assertEquals(ContentState.Loading, awaitItem())
            assertEquals(ContentState.Content(TestPlaces), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        val emptyVm = DiscoverViewModel(FakePlaceRepository(emptyList()), FakePreferencesStore())
        emptyVm.contentState.test {
            assertEquals(ContentState.Loading, awaitItem())
            advanceUntilIdle()
            assertEquals(ContentState.Empty, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        val errorVm = DiscoverViewModel(
            FakePlaceRepository(placesFlow = flow { throw IOException("offline") }),
            FakePreferencesStore()
        )
        errorVm.contentState.test {
            assertEquals(ContentState.Loading, awaitItem())
            advanceUntilIdle()
            assertTrue(awaitItem() is ContentState.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loading settles once the first non-empty emission arrives`() = runTest {
        val vm = DiscoverViewModel(FakePlaceRepository(), FakePreferencesStore())
        vm.contentState.test {
            assertEquals(ContentState.Loading, awaitItem())
            assertTrue(awaitItem() is ContentState.Content)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `available categories only include heritage groups with content`() = runTest {
        val vm = DiscoverViewModel(FakePlaceRepository(), FakePreferencesStore())
        vm.availableCategories.test {
            skipItems(1)
            val categories = awaitItem()
            assertTrue(categories.contains("All"))
            assertTrue(categories.contains("PAGODA_NEWA"))
            assertTrue(categories.contains("STUPA_MONASTERY"))
            assertTrue(categories.contains("SACRED_LANDSCAPE"))
            // No water architecture and no shikhara content was loaded, so the
            // UI must not offer those filters as if they had results.
            assertFalse(categories.contains("SACRED_WATER"))
            assertFalse(categories.contains("SHIKHARA_GRANTHAKUTA"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `recommended order preserves curated order instead of ranking by rating`() = runTest {
        // TestPlaces is ordered Kathmandu (4.8) then Lumbini (4.9). Sorting by
        // rating would put Lumbini first and turn an unsourced score into a
        // recommendation signal.
        val vm = DiscoverViewModel(FakePlaceRepository(), FakePreferencesStore())
        vm.recommended.test {
            skipItems(1) // stateIn replays its empty initial value first
            assertEquals(
                listOf("kathmandu-durbar", "lumbini-garden"),
                awaitItem().map { it.id }
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `suggestion select clears the query and switches category`() = runTest {
        val vm = DiscoverViewModel(FakePlaceRepository(), FakePreferencesStore())
        vm.onQueryChange("nothing matches this")
        vm.onSuggestionSelect("PAGODA_NEWA")
        assertEquals("", vm.query.value)
        assertEquals(DiscoverCategories.indexOf("PAGODA_NEWA"), vm.selectedCategory.value)
    }
}
