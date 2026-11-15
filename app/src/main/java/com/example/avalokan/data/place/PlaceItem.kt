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

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

/**
 * Room persistence model. Map it to `domain.model.Place` at the repository
 * boundary; UI code should never depend on this type.
 */
@Entity(tableName = "places")
data class PlaceItem(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val meta: String = "",
    val category: String = "",
    val rating: String = "",
    val entryFee: String = "",
    val bestTime: String = "",
    val hours: String = "",
    val badge: String = "",
    val featured: Boolean = false,
    // Getting url to show the image, null if there's nothing in the database
    val imageUrl: String? = null,
    // Heritage context. Every field is optional so older documents keep working.
    val province: String = "",
    val district: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val era: String = "",
    // Free-form so values such as "World Heritage" or "Tentative List" survive.
    val unescoStatus: String = "",
    val heritageType: String = "",
    // Short editorial answer to "why does this place matter?".
    val significance: String = "",
    val livingHeritage: Boolean = false,
    val etiquette: String = "",
    val accessibility: String = "",
    // Provenance. Blank means "not recorded", never "verified".
    val sourceLabel: String = "",
    val sourceUrl: String = "",
    val lastVerifiedAt: String = "",
    val verificationStatus: String = "",
    // Multi-dimensional classification (see HeritageClassification.kt).
    @ColumnInfo(defaultValue = "''") val primaryCategory: String = "",
    @ColumnInfo(defaultValue = "''") val secondaryCategories: List<String> = emptyList(),
    @ColumnInfo(defaultValue = "''") val typologies: List<String> = emptyList(),
    @ColumnInfo(defaultValue = "''") val attributes: List<String> = emptyList(),
    @ColumnInfo(defaultValue = "''") val tags: List<String> = emptyList(),
    @ColumnInfo(defaultValue = "''") val locality: String = "",
    @ColumnInfo(defaultValue = "''") val relatedPlaceIds: List<String> = emptyList(),
    @ColumnInfo(defaultValue = "''") val relatedEventIds: List<String> = emptyList()
)
