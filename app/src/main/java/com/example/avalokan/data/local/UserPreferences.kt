package com.example.avalokan.data.local

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val PREFS_NAME = "user_prefs"

private val Context.userPrefsStore by preferencesDataStore(
    name = PREFS_NAME,
    corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() }
)

class UserPreferences(private val context: Context) {

    companion object {
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val THEME_CHOICE = stringPreferencesKey("theme_choice")

        const val THEME_SYSTEM = "system"
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
    }

    val notificationsEnabled: Flow<Boolean> = context.userPrefsStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs -> prefs[NOTIFICATIONS_ENABLED] ?: true }

    val themeChoice: Flow<String> = context.userPrefsStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs -> prefs[THEME_CHOICE] ?: THEME_SYSTEM }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.userPrefsStore.edit { prefs -> prefs[NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setThemeChoice(choice: String) {
        context.userPrefsStore.edit { prefs -> prefs[THEME_CHOICE] = choice }
    }
}