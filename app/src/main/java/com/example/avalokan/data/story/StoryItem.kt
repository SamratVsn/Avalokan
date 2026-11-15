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

package com.example.avalokan.data.story

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

/**
 * Room persistence model. Map it to `domain.model.Story` at the repository
 * boundary; UI code should never depend on this type.
 */
@Entity(tableName = "stories")
data class StoryItem(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val author: String = "",
    val date: String = "",
    val readTime: String = "",
    val badge: String = "",
    // Editorial provenance. Blank means "not recorded", never "verified".
    val curator: String = "",
    val sourceLabel: String = "",
    val sourceUrl: String = "",
    val lastVerifiedAt: String = "",
    val verificationStatus: String = "",
    // Places referenced by this story, stored through the List<String> converter.
    val placeIds: List<String> = emptyList(),
    // Editorial classification.
    @ColumnInfo(defaultValue = "''") val primaryCategory: String = "",
    @ColumnInfo(defaultValue = "''") val secondaryCategories: List<String> = emptyList(),
    @ColumnInfo(defaultValue = "''") val typologies: List<String> = emptyList(),
    @ColumnInfo(defaultValue = "''") val tags: List<String> = emptyList(),
    @ColumnInfo(defaultValue = "''") val relatedEventIds: List<String> = emptyList(),
    @ColumnInfo(defaultValue = "''") val period: String = ""
)
