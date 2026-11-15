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

package com.example.avalokan.data

import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.example.avalokan.data.event.EventDao
import com.example.avalokan.data.event.EventItem
import com.example.avalokan.data.place.PlaceDao
import com.example.avalokan.data.place.PlaceItem
import com.example.avalokan.data.story.StoryDao
import com.example.avalokan.data.story.StoryItem

/**
 * Disposable, Firestore-backed content cache.
 *
 * CONTRACT: every table here is derived from Firestore and can be rebuilt from
 * the next snapshot. Destructive migrations are therefore acceptable while that
 * remains true. If user-created data is ever stored in this database, remove
 * `fallbackToDestructiveMigration()` and add real `Migration`s instead.
 *
 * Schemas are exported to `app/schemas/` so future migrations can be written
 * from the current version onward.
 */
@ColumnTypeConverters(Converters::class)
@Database(
    entities = [PlaceItem::class, EventItem::class, StoryItem::class],
    version = 10,
    exportSchema = true,
)
abstract class AvalokanDatabase : RoomDatabase() {
    abstract fun placeDao(): PlaceDao
    abstract fun eventDao(): EventDao
    abstract fun storyDao(): StoryDao
}