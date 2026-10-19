package com.example.avalokan

import com.example.avalokan.data.local.PreferencesStore
import com.example.avalokan.data.place.PlaceItem
import com.example.avalokan.data.place.PlaceRepository
import com.example.avalokan.data.user.UserProfile
import com.example.avalokan.data.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

/** Places used by the view-model tests: one Historical, one Cultural. */
internal val TestPlaces = listOf(
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

internal class FakePlaceRepository(
    private val places: List<PlaceItem> = TestPlaces
) : PlaceRepository {
    override fun observePlaces(): Flow<List<PlaceItem>> = flowOf(places)
    override fun observePlace(id: String): Flow<PlaceItem?> =
        flowOf(places.find { it.id == id })

    override suspend fun clearCache() = Unit

    override suspend fun refresh() = Unit
}

internal class FakePreferencesStore : PreferencesStore {
    private val saved = MutableStateFlow(emptySet<String>())
    private val avatar = MutableStateFlow<String?>(null)

    override val notificationsEnabled: Flow<Boolean> = flowOf(true)
    override val themeChoice: Flow<String> = flowOf(PreferencesStore.THEME_SYSTEM)
    override val avatarUri: Flow<String?> = avatar
    override val savedPlaceIds: Flow<Set<String>> = saved

    override suspend fun setNotificationsEnabled(enabled: Boolean) = Unit
    override suspend fun setThemeChoice(choice: String) = Unit

    override suspend fun setAvatarUri(uri: String?) {
        avatar.value = uri
    }

    override suspend fun setSavedPlaceIds(ids: Set<String>) {
        saved.value = ids
    }

    override suspend fun clearAll() {
        saved.value = emptySet()
        avatar.value = null
    }
}

internal class FakeUserRepository : UserRepository {
    override fun observeUser(uid: String): Flow<UserProfile?> = flowOf(null)
    override suspend fun saveUser(profile: UserProfile) = Unit
}
