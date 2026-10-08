package com.example.avalokan.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _userName = MutableStateFlow("Guest")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _bio = MutableStateFlow("Exploring the heritage of Nepal")
    val bio: StateFlow<String> = _bio.asStateFlow()

    private val _stats = MutableStateFlow(ProfileStats())
    val stats: StateFlow<ProfileStats> = _stats.asStateFlow()

    val avatarUri: StateFlow<String?> = userPreferences.avatarUri
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun setAvatarUri(uri: String?) {
        viewModelScope.launch { userPreferences.setAvatarUri(uri) }
    }

    fun updateName(name: String) {
        val trimmed = name.trim()
        if (trimmed.isNotEmpty()) _userName.value = trimmed
    }

    fun updateBio(bio: String) {
        _bio.value = bio.trim()
    }

    private val _collectionIds = MutableStateFlow(listOf("kathmandu-durbar", "boudha-stupa"))
    val collectionIds: StateFlow<List<String>> = _collectionIds.asStateFlow()
}