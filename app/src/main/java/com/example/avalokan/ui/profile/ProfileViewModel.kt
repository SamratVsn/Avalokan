package com.example.avalokan.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.local.UserPreferences
import com.example.avalokan.data.place.PlaceItem
import com.example.avalokan.data.place.PlaceRepository
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Known places for resolving saved IDs offline (mirrors nav IDs). */
private val KnownPlaces = listOf(
    PlaceItem("kathmandu-durbar", "Kathmandu Durbar Square", "", "Kathmandu • Historical Site", "Historical", "4.8"),
    PlaceItem("patan-durbar", "Patan Durbar Square", "", "Lalitpur • Historical Site", "Historical", "4.8"),
    PlaceItem("swayambhu", "Swayambhu", "", "Kathmandu • Spiritual Site", "Historical", "4.7"),
    PlaceItem("boudha-stupa", "Boudha Stupa", "", "Boudhanath • Spiritual Site", "Cultural", "4.9"),
    PlaceItem("lumbini-garden", "Lumbini Garden", "", "Lumbini • Spiritual Site", "Cultural", "4.9"),
    PlaceItem("bhaktapur-pottery", "Bhaktapur Pottery Square", "", "Bhaktapur • Craft Quarter", "Cultural", "4.7")
)

data class ProfileStats(
    val visited: Int = 0,
    val saved: Int = 0,
    val events: Int = 0
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    placeRepository: PlaceRepository
) : ViewModel() {
    private val _userName = MutableStateFlow("Guest")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _bio = MutableStateFlow("Exploring the heritage of Nepal")
    val bio: StateFlow<String> = _bio.asStateFlow()

    private val _stats = MutableStateFlow(ProfileStats())
    val stats: StateFlow<ProfileStats> = _stats.asStateFlow()

    /** Saved places resolved against known + remote data. */
    val savedPlaces: StateFlow<List<PlaceItem>> = combine(
        placeRepository.observePlaces(),
        userPreferences.savedPlaceIds
    ) { remote, ids ->
        val all = (remote + KnownPlaces).distinctBy { it.id }
        ids.mapNotNull { id -> all.find { it.id == id } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val avatarUri: StateFlow<String?> = userPreferences.avatarUri
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun setAvatarUri(uri: String?) {
        viewModelScope.launch { userPreferences.setAvatarUri(uri) }
    }

    /** Uploads to Firebase Storage, then persists the download URL. */
    fun uploadAvatar(localUri: String) {
        setAvatarUri(localUri) // instant preview while the upload runs
        FirebaseStorage.getInstance().reference
            .child("avatars/${System.currentTimeMillis()}.jpg")
            .putFile(Uri.parse(localUri))
            .continueWithTask { task ->
                if (!task.isSuccessful) throw task.exception ?: Exception("Upload failed")
                task.result.storage.downloadUrl
            }
            .addOnSuccessListener { url -> setAvatarUri(url.toString()) }
    }

    fun updateName(name: String) {
        val trimmed = name.trim()
        if (trimmed.isNotEmpty()) _userName.value = trimmed
    }

    fun updateBio(bio: String) {
        _bio.value = bio.trim()
    }
}