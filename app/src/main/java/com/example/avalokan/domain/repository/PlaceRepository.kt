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

package com.example.avalokan.domain.repository

import com.example.avalokan.domain.model.Place
import kotlinx.coroutines.flow.Flow


//Content contract used by the UI. Implementations may use Room, Firestore, or a fake without any screen knowing the difference.
interface PlaceRepository {
    fun observePlaces(): Flow<List<Place>>
    fun observePlace(id: String): Flow<Place?>
    suspend fun clearCache()

    /** Forces a server fetch; snapshot listeners keep streaming afterwards. */
    suspend fun refresh()
}
