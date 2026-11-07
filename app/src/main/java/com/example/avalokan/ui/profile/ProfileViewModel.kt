package com.example.avalokan.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.local.AvatarStore
import com.example.avalokan.data.local.PreferencesStore
import com.example.avalokan.data.user.UserProfile
import com.example.avalokan.data.user.UserRepository
import com.example.avalokan.domain.model.Event
import com.example.avalokan.domain.model.Place
import com.example.avalokan.domain.repository.EventRepository
import com.example.avalokan.domain.repository.PlaceRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileStats(
    val visited: Int = 0,
    val saved: Int = 0,
    val favorites: Int = 0
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPreferences: PreferencesStore,
    placeRepository: PlaceRepository,
    eventRepository: EventRepository,
    private val userRepository: UserRepository,
    private val avatarStore: AvatarStore,
) : ViewModel() {

    /**
     * Current uid, then every change to it. Driven by auth state rather than
     * read once at construction, so a user who signs in after this ViewModel
     * exists still gets their profile — and a sign-out stops feeding it the
     * previous user's document.
     */
    private val uid: Flow<String?> = callbackFlow {
        val auth = FirebaseAuth.getInstance()
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.uid)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.distinctUntilChanged()

    private val remoteProfile: StateFlow<UserProfile?> = uid
        .flatMapLatest { id ->
            if (id == null) flowOf(null) else userRepository.observeUser(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _userName = MutableStateFlow("Guest")
    val userName: StateFlow<String> = combine(_userName, remoteProfile) { local, remote ->
        remote?.displayName?.ifBlank { local } ?: local
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "Guest")

    private val _bio = MutableStateFlow("")
    val bio: StateFlow<String> = combine(_bio, remoteProfile) { local, remote ->
        remote?.takeIf { it.bio.isNotBlank() }?.bio ?: local
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    /**
     * Local avatars win: they are the only ones this device can actually read.
     * [UserRepository] only exposes remote `http(s)` avatar URLs, so a legacy
     * `content://` value in Firestore can never become the displayed avatar.
     */
    val avatarUri: StateFlow<String?> = combine(
        userPreferences.avatarLocalUri, remoteProfile
    ) { local, remote -> local?.takeIf { it.isNotBlank() } ?: remote?.avatarUrl }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val savedPlaces: StateFlow<List<Place>> = combine(
        placeRepository.observePlaces(),
        userPreferences.savedPlaceIds
    ) { remote, ids ->
        ids.mapNotNull { id -> remote.find { it.id == id } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val favoriteEvents: StateFlow<List<Event>> = combine(
        eventRepository.observeEvents(),
        userPreferences.favoriteEventIds
    ) { remote, ids ->
        ids.mapNotNull { id -> remote.find { it.id == id } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /**
     * Only explicit "Mark as visited" actions are stored. Opening a detail
     * screen is not a visit, so it can never inflate this list.
     */
    val visitedPlaces: StateFlow<List<Place>> = combine(
        placeRepository.observePlaces(),
        userPreferences.visitedPlaceIds
    ) { remote, ids ->
        ids.mapNotNull { id -> remote.find { it.id == id } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /**
     * Derived from [visitedPlaces], [savedPlaces] and [favoriteEvents] so every
     * counter matches the lists shown below it.
     */
    val stats: StateFlow<ProfileStats> = combine(
        visitedPlaces, savedPlaces, favoriteEvents
    ) { visited, places, events ->
        ProfileStats(
            visited = visited.size,
            saved = places.size,
            favorites = events.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileStats())

    /**
     * Copies a picked `content://` URI into private storage, then saves the
     * resulting `file://` URI locally. A failed copy keeps the previous avatar.
     */
    fun setAvatar(uri: String?) {
        viewModelScope.launch {
            val localUri = if (uri == null) {
                avatarStore.delete()
                null
            } else {
                avatarStore.import(uri) ?: return@launch
            }
            userPreferences.setAvatarLocalUri(localUri)
        }
    }

    fun updateName(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        _userName.value = trimmed
        persist(displayName = trimmed)
    }

    fun updateBio(bio: String) {
        val trimmed = bio.trim()
        _bio.value = trimmed
        persist(bio = trimmed)
    }

    private fun persist(displayName: String? = null, bio: String? = null) {
        val id = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            userRepository.saveUser(
                UserProfile(
                    uid = id,
                    displayName = displayName ?: userName.value,
                    bio = bio ?: this@ProfileViewModel.bio.value,
                    // Avatars are device-local. Never send a file:// URI to
                    // Firestore; the repository also rejects non-http(s) values.
                    avatarUrl = null,
                )
            )
        }
    }
}