package com.example.avalokan.data.local

import kotlinx.coroutines.flow.Flow

interface PreferencesStore {

    companion object {
        const val THEME_SYSTEM = "system"
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
    }

    val notificationsEnabled: Flow<Boolean>
    val themeChoice: Flow<String>
    val avatarUri: Flow<String?>
    val savedPlaceIds: Flow<Set<String>>

    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun setThemeChoice(choice: String)
    suspend fun setAvatarUri(uri: String?)
    suspend fun setSavedPlaceIds(ids: Set<String>)
    suspend fun clearAll()
}
