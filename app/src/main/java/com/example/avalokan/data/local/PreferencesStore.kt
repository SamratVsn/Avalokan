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
