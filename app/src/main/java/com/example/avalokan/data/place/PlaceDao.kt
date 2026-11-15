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

package com.example.avalokan.data.place

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {
    @Query("SELECT * FROM places ORDER BY id")
    fun observeAll(): Flow<List<PlaceItem>>

    @Query("SELECT * FROM places WHERE id = :id")
    fun observeById(id: String): Flow<PlaceItem?>

    @Upsert
    suspend fun upsertAll(item: List<PlaceItem>)

    @Query("DELETE FROM places")
    suspend fun clearAll()

    @Query("DELETE FROM places WHERE id = :id")
    suspend fun deleteById(id: String)

    /** Replaces the whole cache in one Room transaction, so observers see one update. */
    @Transaction
    suspend fun replaceAll(items: List<PlaceItem>) {
        clearAll()
        upsertAll(items)
    }
}
