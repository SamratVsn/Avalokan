package com.example.avalokan

import com.example.avalokan.data.local.AvatarStore
import com.example.avalokan.data.local.PreferencesStore
import com.example.avalokan.data.user.UserProfile
import com.example.avalokan.data.user.UserRepository
import com.example.avalokan.domain.model.Event
import com.example.avalokan.domain.model.Place
import com.example.avalokan.domain.repository.EventRepository
import com.example.avalokan.domain.repository.PlaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

/** Places used by the view-model tests: two typologies, one shared group. */
internal val TestPlaces = listOf(
    Place(
        id = "kathmandu-durbar",
        name = "Kathmandu Durbar Square",
        description = "desc",
        meta = "Kathmandu • Historical Site",
        category = "Historical",
        rating = "4.8",
        typologies = listOf("DURBAR", "PAGODA"),
        primaryCategory = "HISTORIC_URBAN_HERITAGE"
    ),
    Place(
        id = "lumbini-garden",
        name = "Lumbini Garden",
        description = "desc",
        meta = "Lumbini • Spiritual Site",
        category = "Cultural",
        rating = "4.9",
        typologies = listOf("BUDDHIST_SACRED_SITE", "PILGRIMAGE_LANDSCAPE"),
        primaryCategory = "BUDDHIST_HERITAGE_SITE"
    )
)

internal val TestEvents = listOf(
    Event(
        id = "dashain-festival",
        title = "Dashain Festival",
        description = "desc",
        meta = "Kathmandu • Cultural",
        date = "Oct 12, 2026"
    ),
    Event(
        id = "heritage-walk",
        title = "Heritage Walk",
        description = "desc",
        meta = "Bhaktapur • Tour",
        date = "Nov 3, 2026"
    )
)

internal class FakeEventRepository(
    private val events: List<Event> = TestEvents
) : EventRepository {
    override fun observeEvents(): Flow<List<Event>> = flowOf(events)
    override fun observeEvent(id: String): Flow<Event?> =
        flowOf(events.find { it.id == id })

    override suspend fun clearCache() = Unit
    override suspend fun refresh() = Unit
}

internal class FakePlaceRepository(
    private val places: List<Place> = TestPlaces
) : PlaceRepository {
    override fun observePlaces(): Flow<List<Place>> = flowOf(places)
    override fun observePlace(id: String): Flow<Place?> =
        flowOf(places.find { it.id == id })

    override suspend fun clearCache() = Unit

    override suspend fun refresh() = Unit
}

internal class FakePreferencesStore : PreferencesStore {
    private val saved = MutableStateFlow(emptySet<String>())
    private val favorites = MutableStateFlow(emptySet<String>())
    private val visited = MutableStateFlow(emptySet<String>())
    private val avatar = MutableStateFlow<String?>(null)

    override val notificationsEnabled: Flow<Boolean> = flowOf(true)
    override val themeChoice: Flow<String> = flowOf(PreferencesStore.THEME_SYSTEM)
    override val avatarLocalUri: Flow<String?> = avatar
    override val savedPlaceIds: Flow<Set<String>> = saved
    override val favoriteEventIds: Flow<Set<String>> = favorites
    override val visitedPlaceIds: Flow<Set<String>> = visited

    override suspend fun setNotificationsEnabled(enabled: Boolean) = Unit
    override suspend fun setThemeChoice(choice: String) = Unit

    override suspend fun setAvatarLocalUri(uri: String?) {
        avatar.value = uri
    }

    override suspend fun setSavedPlaceIds(ids: Set<String>) {
        saved.value = ids
    }

    override suspend fun setFavoriteEventIds(ids: Set<String>) {
        favorites.value = ids
    }

    override suspend fun setVisitedPlaceIds(ids: Set<String>) {
        visited.value = ids
    }

    override suspend fun clearAll() {
        saved.value = emptySet()
        favorites.value = emptySet()
        visited.value = emptySet()
        avatar.value = null
    }
}

internal class FakeUserRepository : UserRepository {
    override fun observeUser(uid: String): Flow<UserProfile?> = flowOf(null)
    override suspend fun saveUser(profile: UserProfile) = Unit
}

internal class FakeAvatarStore(
    var localUri: String? = null,
) : AvatarStore {
    var importedUri: String? = null
    var deleted = false

    override suspend fun import(uri: String): String? {
        importedUri = uri
        return localUri
    }

    override suspend fun delete() {
        deleted = true
    }
}
