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

package com.example.avalokan.domain.model

/**
 * Comparison order for place listings. [RECOMMENDED] keeps the repository's
 * curated order (and search relevance, when a query is active); the others are
 * explicit user choices.
 *
 * Region and UNESCO are the only extra dimensions the current data supports
 * reliably. Era sorting is deliberately absent until eras are normalised.
 */
enum class PlaceSort {
    RECOMMENDED,
    NAME,
    REGION,
    UNESCO;

    companion object {
        /** Unknown or missing saved values fall back to the curated order. */
        fun fromName(value: String?): PlaceSort =
            entries.firstOrNull { it.name == value } ?: RECOMMENDED
    }
}

/**
 * Applies [sort] to an already filtered list. Blank regions sort last so they
 * never push real regions out of the way, and every comparator ends with the
 * name (then id) so the result is deterministic.
 */
fun List<Place>.sortedFor(sort: PlaceSort): List<Place> {
    val byName = compareBy<Place, String>(String.CASE_INSENSITIVE_ORDER) { it.name }
        .thenBy { it.id }
    return when (sort) {
        PlaceSort.RECOMMENDED -> this
        PlaceSort.NAME -> sortedWith(byName)
        PlaceSort.REGION -> sortedWith(
            compareBy<Place, String>(String.CASE_INSENSITIVE_ORDER) {
                it.regionLabel.ifBlank { "\uFFFF" }
            }.then(byName)
        )
        PlaceSort.UNESCO -> sortedWith(
            compareByDescending<Place> { it.isUnescoListed }
                .then(byName)
        )
    }
}
