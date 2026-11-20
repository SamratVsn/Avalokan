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

package com.example.avalokan.ui.common

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.avalokan.R

/**
 * The legacy `place.category` taxonomy.
 *
 * These five strings are the broad editorial buckets stored in Firestore, so
 * they must stay stable for backward compatibility. They deliberately overlap
 * — a living temple courtyard can read as both Cultural and Spiritual — and are
 * therefore a browse shortcut, not a scientific classification. The precise,
 * non-overlapping taxonomy is [com.example.avalokan.domain.classification.HeritageGroup],
 * used by Discover's filters.
 *
 * [definitionRes] explains the bucket in the category page header so users know
 * why a place belongs there; unknown stored values keep their raw name and get
 * no fabricated definition.
 */
data class PlaceCategoryDefinition(
    @StringRes val titleRes: Int,
    @StringRes val definitionRes: Int
)

private val PlaceCategoryDefinitions = mapOf(
    "historical" to PlaceCategoryDefinition(
        R.string.category_historical,
        R.string.category_historical_definition
    ),
    "cultural" to PlaceCategoryDefinition(
        R.string.category_cultural,
        R.string.category_cultural_definition
    ),
    "spiritual" to PlaceCategoryDefinition(
        R.string.category_spiritual,
        R.string.category_spiritual_definition
    ),
    "nature" to PlaceCategoryDefinition(
        R.string.category_nature,
        R.string.category_nature_definition
    ),
    "adventure" to PlaceCategoryDefinition(
        R.string.category_adventure,
        R.string.category_adventure_definition
    )
)

fun placeCategoryDefinition(category: String): PlaceCategoryDefinition? =
    PlaceCategoryDefinitions[category.trim().lowercase()]

/**
 * Display name for a place's legacy `category` value, shared by the category
 * page and Home. Unknown values are shown as stored rather than hidden, so a
 * curator's new category still has a working entry point.
 */
@Composable
fun placeCategoryTitle(category: String): String {
    val definition = placeCategoryDefinition(category)
    return if (definition != null) {
        stringResource(definition.titleRes)
    } else {
        category.trim().ifBlank { stringResource(R.string.explore_places) }
    }
}

/** The category's definition, or null for categories this build cannot name. */
@Composable
fun placeCategoryDefinitionText(category: String): String? =
    placeCategoryDefinition(category)?.let { stringResource(it.definitionRes) }
