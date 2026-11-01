package com.example.avalokan.data.local

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.avalokan.data.security.isSafeDocumentId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val PREFS_NAME = "user_prefs"
private val AVATAR_URI = stringPreferencesKey("avatar_uri")
private val SAVED_PLACE_IDS = stringPreferencesKey("saved_place_ids")
private val FAVORITE_EVENT_IDS = stringPreferencesKey("favorite_event_ids")
private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
private val THEME_CHOICE = stringPreferencesKey("theme_choice")

private val Context.userPrefsStore by preferencesDataStore(
    name = PREFS_NAME,
    corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() }
)

class UserPreferences(private val context: Context) : PreferencesStore {

    override val notificationsEnabled: Flow<Boolean> = context.userPrefsStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs -> prefs[NOTIFICATIONS_ENABLED] ?: true }

    override val themeChoice: Flow<String> = context.userPrefsStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs -> prefs[THEME_CHOICE] ?: PreferencesStore.THEME_SYSTEM }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.userPrefsStore.edit { prefs -> prefs[NOTIFICATIONS_ENABLED] = enabled }
    }

    override suspend fun setThemeChoice(choice: String) {
        if (choice !in setOf(
                PreferencesStore.THEME_SYSTEM,
                PreferencesStore.THEME_LIGHT,
                PreferencesStore.THEME_DARK,
            )
        ) {
            return
        }
        context.userPrefsStore.edit { prefs -> prefs[THEME_CHOICE] = choice }
    }

    override val avatarLocalUri: Flow<String?> = context.userPrefsStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        // Older builds stored the picker's content:// URI here. It is not
        // readable after a process restart or on another device, so ignore it.
        .map { prefs -> prefs[AVATAR_URI]?.takeIf { it.startsWith("file://") } }

    override val savedPlaceIds: Flow<Set<String>> = context.userPrefsStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs ->
            prefs[SAVED_PLACE_IDS]
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                ?.toSet()
                .orEmpty()
        }

    override suspend fun setSavedPlaceIds(ids: Set<String>) {
        val safe = ids.filter { isSafeDocumentId(it) }.toSet()
        context.userPrefsStore.edit { prefs ->
            prefs[SAVED_PLACE_IDS] = safe.joinToString(",")
        }
    }

    override val favoriteEventIds: Flow<Set<String>> = context.userPrefsStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs ->
            prefs[FAVORITE_EVENT_IDS]
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                ?.toSet()
                .orEmpty()
        }

    override suspend fun setFavoriteEventIds(ids: Set<String>) {
        val safe = ids.filter { isSafeDocumentId(it) }.toSet()
        context.userPrefsStore.edit { prefs ->
            prefs[FAVORITE_EVENT_IDS] = safe.joinToString(",")
        }
    }

    override suspend fun setAvatarLocalUri(uri: String?) {
        val local = uri?.takeIf { it.startsWith("file://") }
        context.userPrefsStore.edit { prefs ->
            if (local == null) prefs.remove(AVATAR_URI) else prefs[AVATAR_URI] = local
        }
    }

    override suspend fun clearAll() {
        context.userPrefsStore.edit { it.clear() }
    }
}