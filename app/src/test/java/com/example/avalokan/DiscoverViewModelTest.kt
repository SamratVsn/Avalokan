package com.example.avalokan

import app.cash.turbine.test
import com.example.avalokan.data.local.PreferencesStore
import com.example.avalokan.data.place.PlaceItem
import com.example.avalokan.data.place.PlaceRepository
import com.example.avalokan.ui.discover.DiscoverViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
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

private val TestSites = listOf(
    PlaceItem(
        id = "kathmandu-durbar",
        name = "Kathmandu Durbar Square",
        description = "desc",
        meta = "Kathmandu • Historical Site",
        category = "Historical",
        rating = "4.8"
    ),
    PlaceItem(
        id = "lumbini-garden",
        name = "Lumbini Garden",
        description = "desc",
        meta = "Lumbini • Spiritual Site",
        category = "Cultural",
        rating = "4.9"
    )
)

private class FakePlaceRepository : PlaceRepository {
    override fun observePlaces(): Flow<List<PlaceItem>> = flowOf(TestSites)
    override fun observePlace(id: String): Flow<PlaceItem?> =
        flowOf(TestSites.find { it.id == id })

    override suspend fun clearCache() = Unit

    override suspend fun refresh() = Unit
}

private class FakePreferencesStore : PreferencesStore {
    private val saved = MutableStateFlow(emptySet<String>())
    override val notificationsEnabled: Flow<Boolean> = flowOf(true)
    override val themeChoice: Flow<String> = flowOf(PreferencesStore.THEME_SYSTEM)
    override val avatarUri: Flow<String?> = flowOf(null)
    override val savedPlaceIds: Flow<Set<String>> = saved
    override suspend fun setNotificationsEnabled(enabled: Boolean) = Unit
    override suspend fun setThemeChoice(choice: String) = Unit
    override suspend fun setAvatarUri(uri: String?) = Unit
    override suspend fun setSavedPlaceIds(ids: Set<String>) {
        saved.value = ids
    }

    override suspend fun clearAll() {
        saved.value = emptySet()
    }
}

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
}
