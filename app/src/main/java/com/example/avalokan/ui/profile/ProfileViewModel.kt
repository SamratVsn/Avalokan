package com.example.avalokan.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.local.PreferencesStore
import com.example.avalokan.data.place.PlaceItem
import com.example.avalokan.data.place.PlaceRepository
import com.example.avalokan.data.user.UserProfile
import com.example.avalokan.data.user.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileStats(
    val visited: Int = 0,
    val saved: Int = 0,
    val events: Int = 0
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPreferences: PreferencesStore,
    placeRepository: PlaceRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val uid: String? = FirebaseAuth.getInstance().currentUser?.uid

    private val remoteProfile: StateFlow<UserProfile?> = (
            uid?.let { userRepository.observeUser(it) } ?: flowOf(null)
            ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _userName = MutableStateFlow("Guest")
    val userName: StateFlow<String> = combine(_userName, remoteProfile) { local, remote ->
        remote?.displayName?.ifBlank { local } ?: local
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "Guest")

    private val _bio = MutableStateFlow("Exploring the heritage of Nepal")
    val bio: StateFlow<String> = combine(_bio, remoteProfile) { local, remote ->
        remote?.takeIf { it.bio.isNotBlank() }?.bio ?: local
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "Exploring the heritage of Nepal")

    private val _stats = MutableStateFlow(ProfileStats())
    val stats: StateFlow<ProfileStats> = _stats.asStateFlow()

    val avatarUri: StateFlow<String?> = combine(
        userPreferences.avatarUri, remoteProfile
    ) { local, remote -> remote?.avatarUrl ?: local }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val savedPlaces: StateFlow<List<PlaceItem>> = combine(
        placeRepository.observePlaces(),
        userPreferences.savedPlaceIds
    ) { remote, ids ->
        ids.mapNotNull { id -> remote.find { it.id == id } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setAvatarUri(uri: String?) {
        viewModelScope.launch { userPreferences.setAvatarUri(uri) }
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
        val id = uid ?: return
        viewModelScope.launch {
            userRepository.saveUser(
                UserProfile(
                    uid = id,
                    displayName = displayName ?: userName.value,
                    bio = bio ?: this@ProfileViewModel.bio.value,
                    avatarUrl = avatarUri.value
                )
            )
        }
    }
}