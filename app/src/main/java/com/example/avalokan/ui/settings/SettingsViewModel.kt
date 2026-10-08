package com.example.avalokan.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val notificationsEnabled: Boolean = true,
    val themeChoice: String = UserPreferences.THEME_SYSTEM
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        userPreferences.notificationsEnabled,
        userPreferences.themeChoice
    ) { notifications, theme ->
        SettingsUiState(
            notificationsEnabled = notifications,
            themeChoice = theme
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
}