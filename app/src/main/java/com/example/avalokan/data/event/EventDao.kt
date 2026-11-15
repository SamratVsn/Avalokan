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

package com.example.avalokan.data.event

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow

//Functions to get data from Database
@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY id")
    fun observeAll(): Flow<List<EventItem>>

    @Query("SELECT * FROM events WHERE id = :id")
    fun observeById(id: String): Flow<EventItem?>

    @Upsert //Upsert = Update or insert
    suspend fun upsertAll(items: List<EventItem>)

    @Query("DELETE FROM events")
    suspend fun clearAll()

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteById(id: String)

    /** Replaces the whole cache in one Room transaction, so observers see one update. */
    @Transaction
    suspend fun replaceAll(items: List<EventItem>) {
        clearAll()
        upsertAll(items)
    }
}
