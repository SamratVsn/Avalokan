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

    /** A `file://` URI inside app-private storage, never a `content://` picker URI. */
    val avatarLocalUri: Flow<String?>
    val savedPlaceIds: Flow<Set<String>>
    val favoriteEventIds: Flow<Set<String>>

    /**
     * Places the user explicitly marked as visited. Empty by default; a place
     * detail view is never counted as a visit on its own.
     */
    val visitedPlaceIds: Flow<Set<String>>

    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun setThemeChoice(choice: String)
    suspend fun setAvatarLocalUri(uri: String?)
    suspend fun setSavedPlaceIds(ids: Set<String>)
    suspend fun setFavoriteEventIds(ids: Set<String>)
    suspend fun setVisitedPlaceIds(ids: Set<String>)
    suspend fun clearAll()
}
