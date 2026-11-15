/*
 * Copyright (C) 2026 Samrat Parajuli
 *
 * This file is part of Avalokan.
 *
 * Avalokan is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * Avalokan is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Avalokan. If not, see <https://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */

package com.example.avalokan.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.auth.AuthRepository
import com.example.avalokan.data.local.AvatarStore
import com.example.avalokan.data.local.PreferencesStore
import com.example.avalokan.domain.repository.EventRepository
import com.example.avalokan.domain.repository.PlaceRepository
import com.example.avalokan.domain.repository.StoryRepository
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
    private val storyRepository: StoryRepository,
    private val authRepository: AuthRepository,
    private val avatarStore: AvatarStore,
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
            storyRepository.clearCache()
        }
    }

    fun resetSettings() {
        viewModelScope.launch {
            userPreferences.clearAll()
            // The local avatar is not part of DataStore; remove its file too.
            avatarStore.delete()
        }
    }

    fun signOut() {
        authRepository.signOut()
    }
}