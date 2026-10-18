package com.example.avalokan.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.auth.AuthRepository
import com.example.avalokan.data.event.EventRepository
import com.example.avalokan.data.local.PreferencesStore
import com.example.avalokan.data.place.PlaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val notificationsEnabled: Boolean = true,
    val themeChoice: String = PreferencesStore.THEME_SYSTEM,
    val isLoggedIn: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: PreferencesStore,
    private val placeRepository: PlaceRepository,
    private val eventRepository: EventRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        userPreferences.notificationsEnabled,
        userPreferences.themeChoice,
        authRepository.currentUser
    ) { notifications, theme, user ->
        SettingsUiState(
            notificationsEnabled = notifications,
            themeChoice = theme,
            isLoggedIn = user?.isAnonymous == false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun onNotificationsToggle(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setNotificationsEnabled(enabled)
        }
    }

    fun onThemeChoice(choice: String) {
        viewModelScope.launch {
            userPreferences.setThemeChoice(choice)
        }
    }

    fun clearCachedData() {
        viewModelScope.launch {
            placeRepository.clearCache()
            eventRepository.clearCache()
        }
    }

    fun resetSettings() {
        viewModelScope.launch {
            userPreferences.clearAll()
        }
    }

    fun signOut() {
        authRepository.signOut()
    }
}