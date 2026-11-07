package com.example.avalokan

import app.cash.turbine.test
import com.example.avalokan.ui.profile.ProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

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
    fun `saved stat matches the saved places list`() = runTest {
        val prefs = FakePreferencesStore()
        prefs.setSavedPlaceIds(setOf("kathmandu-durbar"))
        val vm = ProfileViewModel(prefs, FakePlaceRepository(), FakeEventRepository(), FakeUserRepository(), FakeAvatarStore())

        vm.savedPlaces.test {
            skipItems(1) // stateIn replays its empty initial value first
            assertEquals(listOf("kathmandu-durbar"), awaitItem().map { it.id })
            cancelAndIgnoreRemainingEvents()
        }

        vm.stats.test {
            skipItems(1)
            assertEquals(1, awaitItem().saved)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `stats follow changes to the saved places list`() = runTest {
        val prefs = FakePreferencesStore()
        val vm = ProfileViewModel(prefs, FakePlaceRepository(), FakeEventRepository(), FakeUserRepository(), FakeAvatarStore())

        vm.stats.test {
            prefs.setSavedPlaceIds(setOf("kathmandu-durbar", "lumbini-garden"))
            // The StateFlow may replay its initial zero value before the update.
            var latest = awaitItem()
            while (latest.saved != 2) latest = awaitItem()
            assertEquals(0, latest.visited)
            assertEquals(0, latest.favorites)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `favorite stat matches the favorite events list`() = runTest {
        val prefs = FakePreferencesStore()
        prefs.setFavoriteEventIds(setOf("dashain-festival"))
        val vm = ProfileViewModel(prefs, FakePlaceRepository(), FakeEventRepository(), FakeUserRepository(), FakeAvatarStore())

        vm.stats.test {
            var latest = awaitItem()
            while (latest.favorites != 1) latest = awaitItem()
            assertEquals(1, latest.favorites)
            assertEquals(0, latest.saved)
            assertEquals(0, latest.visited)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `visited stat is driven by the explicit visited set`() = runTest {
        val prefs = FakePreferencesStore()
        prefs.setVisitedPlaceIds(setOf("kathmandu-durbar"))
        val vm = ProfileViewModel(prefs, FakePlaceRepository(), FakeEventRepository(), FakeUserRepository(), FakeAvatarStore())

        vm.stats.test {
            var latest = awaitItem()
            while (latest.visited != 1) latest = awaitItem()
            assertEquals(1, latest.visited)
            assertEquals(0, latest.saved)
            assertEquals(0, latest.favorites)
            cancelAndIgnoreRemainingEvents()
        }

        vm.visitedPlaces.test {
            // The shared StateFlow may already hold the resolved list, so wait
            // for a non-empty emission instead of assuming an initial replay.
            var latest = awaitItem()
            while (latest.isEmpty()) latest = awaitItem()
            assertEquals(listOf("kathmandu-durbar"), latest.map { it.id })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `all three profile counters come from their own persisted lists`() = runTest {
        val prefs = FakePreferencesStore()
        prefs.setSavedPlaceIds(setOf("lumbini-garden"))
        prefs.setFavoriteEventIds(setOf("heritage-walk"))
        prefs.setVisitedPlaceIds(setOf("kathmandu-durbar"))
        val vm = ProfileViewModel(prefs, FakePlaceRepository(), FakeEventRepository(), FakeUserRepository(), FakeAvatarStore())

        vm.stats.test {
            var latest = awaitItem()
            while (latest.visited != 1 || latest.saved != 1 || latest.favorites != 1) {
                latest = awaitItem()
            }
            assertEquals(1, latest.visited)
            assertEquals(1, latest.saved)
            assertEquals(1, latest.favorites)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `visited ids that match no place are not counted`() = runTest {
        val prefs = FakePreferencesStore()
        prefs.setVisitedPlaceIds(setOf("kathmandu-durbar", "a-place-that-was-deleted"))
        val vm = ProfileViewModel(prefs, FakePlaceRepository(), FakeEventRepository(), FakeUserRepository(), FakeAvatarStore())

        vm.stats.test {
            var latest = awaitItem()
            while (latest.visited != 1) latest = awaitItem()
            assertEquals(1, latest.visited)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saved ids that match no place are not counted`() = runTest {
        val prefs = FakePreferencesStore()
        prefs.setSavedPlaceIds(setOf("kathmandu-durbar", "a-place-that-was-deleted"))
        val vm = ProfileViewModel(prefs, FakePlaceRepository(), FakeEventRepository(), FakeUserRepository(), FakeAvatarStore())

        vm.stats.test {
            skipItems(1)
            assertEquals(1, awaitItem().saved) // only the resolvable id counts
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `picked avatar is copied before its local uri is stored`() = runTest {
        val prefs = FakePreferencesStore()
        val avatarStore = FakeAvatarStore(localUri = "file:///files/avatars/avatar.jpg")
        val vm = ProfileViewModel(
            prefs,
            FakePlaceRepository(),
            FakeEventRepository(),
            FakeUserRepository(),
            avatarStore,
        )

        vm.setAvatar("content://media/picker/42")
        advanceUntilIdle()

        assertEquals("content://media/picker/42", avatarStore.importedUri)
        assertEquals("file:///files/avatars/avatar.jpg", prefs.avatarLocalUri.first())
    }

    @Test
    fun `failed avatar import keeps the previous local uri`() = runTest {
        val prefs = FakePreferencesStore()
        prefs.setAvatarLocalUri("file:///files/avatars/old.jpg")
        val avatarStore = FakeAvatarStore(localUri = null)
        val vm = ProfileViewModel(
            prefs,
            FakePlaceRepository(),
            FakeEventRepository(),
            FakeUserRepository(),
            avatarStore,
        )

        vm.setAvatar("content://media/picker/42")
        advanceUntilIdle()

        assertEquals("file:///files/avatars/old.jpg", prefs.avatarLocalUri.first())
    }

    @Test
    fun `clearing avatar deletes the local file and uri`() = runTest {
        val prefs = FakePreferencesStore()
        prefs.setAvatarLocalUri("file:///files/avatars/old.jpg")
        val avatarStore = FakeAvatarStore()
        val vm = ProfileViewModel(
            prefs,
            FakePlaceRepository(),
            FakeEventRepository(),
            FakeUserRepository(),
            avatarStore,
        )

        vm.setAvatar(null)
        advanceUntilIdle()

        assertTrue(avatarStore.deleted)
        assertNull(prefs.avatarLocalUri.first())
    }
}
