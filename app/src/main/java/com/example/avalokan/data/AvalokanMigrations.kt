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

import androidx.room3.migration.Migration
import androidx.sqlite.execSQL

/**
 * v9 -> v10 adds the multi-dimensional classification columns.
 *
 * These tables are a disposable Firestore cache, but the migration still keeps
 * the cached rows so an offline launch after an app update does not flash an
 * empty Discover/Events screen. Fresh installs create the same columns from the
 * entity definitions.
 */
internal val MIGRATION_9_10 = Migration(9, 10) { connection ->
    val placeColumns = listOf(
        "primaryCategory",
        "secondaryCategories",
        "typologies",
        "attributes",
        "tags",
        "locality",
        "relatedPlaceIds",
        "relatedEventIds",
    )
    placeColumns.forEach { column ->
        connection.execSQL("ALTER TABLE places ADD COLUMN $column TEXT NOT NULL DEFAULT ''")
    }

    val eventColumns = listOf(
        "priceBasis",
        "participation",
        "format",
        "secondaryCategories",
        "tags",
        "relatedPlaceIds",
        "relatedEventIds",
    )
    eventColumns.forEach { column ->
        connection.execSQL("ALTER TABLE events ADD COLUMN $column TEXT NOT NULL DEFAULT ''")
    }

    val storyColumns = listOf(
        "primaryCategory",
        "secondaryCategories",
        "typologies",
        "tags",
        "relatedEventIds",
        "period",
    )
    storyColumns.forEach { column ->
        connection.execSQL("ALTER TABLE stories ADD COLUMN $column TEXT NOT NULL DEFAULT ''")
    }
}
