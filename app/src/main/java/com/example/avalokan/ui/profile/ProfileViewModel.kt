package com.example.avalokan.ui.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class ProfileStats(
    val visited: Int = 12,
    val saved: Int = 45,
    val events: Int = 3
)

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {
    private val _userName = MutableStateFlow("Samrat Parajuli")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _bio = MutableStateFlow("Exploring the heritage of Nepal")
    val bio: StateFlow<String> = _bio.asStateFlow()

    private val _stats = MutableStateFlow(ProfileStats())
    val stats: StateFlow<ProfileStats> = _stats.asStateFlow()

    /** Local avatar photo URI (null = placeholder). Set by the photo picker later. */
    private val _avatarUri = MutableStateFlow<String?>(null)
    val avatarUri: StateFlow<String?> = _avatarUri.asStateFlow()

    fun setAvatarUri(uri: String?) {
        _avatarUri.value = uri
    }

    /** Saved place IDs shown in My Place Collection. */
    private val _collectionIds = MutableStateFlow(listOf("kathmandu-durbar", "boudha-stupa"))
    val collectionIds: StateFlow<List<String>> = _collectionIds.asStateFlow()
}