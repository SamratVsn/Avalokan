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

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.avalokan.R

/**
 * Display name for a place's legacy `category` value, shared by the category
 * page and Home so both always name a category the same way. Unknown values are
 * shown as stored rather than hidden, so a curator's new category still has a
 * working entry point.
 */
@Composable
fun placeCategoryTitle(category: String): String {
    val titleRes = when (category.trim().lowercase()) {
        "historical" -> R.string.category_historical
        "cultural" -> R.string.category_cultural
        "spiritual" -> R.string.category_spiritual
        "nature" -> R.string.category_nature
        "adventure" -> R.string.category_adventure
        else -> null
    }
    return if (titleRes != null) {
        stringResource(titleRes)
    } else {
        category.trim().ifBlank { stringResource(R.string.explore_places) }
    }
}
