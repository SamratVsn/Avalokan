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

import com.example.avalokan.domain.classification.HeritageGroup
import com.example.avalokan.domain.classification.HeritageTypology
import com.example.avalokan.domain.classification.PlaceAttribute
import com.example.avalokan.domain.classification.PlaceBadge
import com.example.avalokan.domain.classification.PlacePrimaryCategory

//A heritage place as the UI understands it.
data class Place(
    val id: String,
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
    val imageUrl: String? = null,
    // Heritage context. Every field is optional so older documents keep working.
    val province: String = "",
    val district: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val era: String = "",
    val unescoStatus: String = "",
    val heritageType: String = "",
    val significance: String = "",
    val livingHeritage: Boolean = false,
    val etiquette: String = "",
    val accessibility: String = "",
    // Multi-dimensional classification. Raw keys are stored so unknown future
    // values survive; typed accessors below ignore what this build cannot name.
    val primaryCategory: String = "",
    val secondaryCategories: List<String> = emptyList(),
    val typologies: List<String> = emptyList(),
    val attributes: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val locality: String = "",
    val relatedPlaceIds: List<String> = emptyList(),
    val relatedEventIds: List<String> = emptyList(),
    // Provenance. Blank means "not recorded", never "verified".
    val sourceLabel: String = "",
    val sourceUrl: String = "",
    val lastVerifiedAt: String = "",
    val verificationStatus: String = "",
) {
    /**
     * Provenance is recorded, not inferred. A blank source means the practical
     * fields on this place have no citation and must not be shown as facts.
     */
    val hasSource: Boolean
        get() = sourceLabel.isNotBlank() || sourceUrl.isNotBlank()

    val hasProvenance: Boolean
        get() = hasSource || lastVerifiedAt.isNotBlank()

    val isMarkedOutdated: Boolean
        get() = verificationStatus.trim().equals("OUTDATED", ignoreCase = true)

    private val isMarkedUnverified: Boolean
        get() = verificationStatus.trim().uppercase() in setOf("UNVERIFIED", "NEEDS_SOURCE")

    /** Practical details are published only when a citation exists and is current. */
    val canShowPracticalInfo: Boolean
        get() = hasProvenance && !isMarkedOutdated && !isMarkedUnverified

    /**
     * A rating is only presentable when a source is recorded for the place and
     * the record is not flagged as unverified or outdated.
     */
    val sourcedRating: String?
        get() = rating.trim().takeIf { it.isNotEmpty() && hasSource && canShowPracticalInfo }

    val primary: PlacePrimaryCategory?
        get() = PlacePrimaryCategory.fromKey(primaryCategory)

    val heritageTypologies: List<HeritageTypology>
        get() = HeritageTypology.fromKeys(typologies)

    val heritageGroups: List<HeritageGroup>
        get() = heritageTypologies.map { it.group }.distinct()

    val placeAttributes: List<PlaceAttribute>
        get() = attributes.mapNotNull(PlaceAttribute::fromKey)

    fun matches(group: HeritageGroup?): Boolean =
        group == null || heritageGroups.contains(group)

    fun matchesQuery(query: String): Boolean = searchRank(query) != null

    /**
     * How well this place matches a free-text [query], or null when it does not
     * match at all. Lower is better, so callers can sort by it:
     * name, then location (meta, locality, district, province), then
     * classification (category, heritage type, typologies, tags), then prose
     * (description, significance). A blank query matches everything equally.
     */
    fun searchRank(query: String): Int? {
        val needle = normalizeSearchText(query)
        if (needle.isEmpty()) return SearchRank.NAME
        fun hit(vararg fields: String) = fields.any { normalizeSearchText(it).contains(needle) }
        fun hitAny(fields: Iterable<String>) = fields.any { normalizeSearchText(it).contains(needle) }
        return when {
            hit(name) -> SearchRank.NAME
            hit(meta, locality, district, province) -> SearchRank.LOCATION
            hit(category, heritageType, primaryCategory, unescoStatus, era) ||
                hitAny(secondaryCategories) || hitAny(typologies) || hitAny(tags) ||
                hitAny(heritageGroups.map { it.key }) -> SearchRank.CLASSIFICATION
            hit(description, significance) -> SearchRank.DESCRIPTION
            else -> null
        }
    }

    object SearchRank {
        const val NAME = 0
        const val LOCATION = 1
        const val CLASSIFICATION = 2
        const val DESCRIPTION = 3
    }

    /** Verified-only badges, in priority order, capped so cards stay readable. */
    fun badges(limit: Int = 2): List<PlaceBadge> {
        val result = buildList {
            if (isUnescoListed) add(PlaceBadge.UNESCO_HERITAGE)
            if (primary == PlacePrimaryCategory.BUDDHIST_HERITAGE_SITE) {
                add(PlaceBadge.BUDDHIST_HERITAGE)
            }
            if (heritageGroups.contains(HeritageGroup.PAGODA_NEWA)) {
                add(PlaceBadge.NEWA_ARCHITECTURE)
            }
            if (heritageGroups.contains(HeritageGroup.SACRED_WATER)) {
                add(PlaceBadge.SACRED_WATER_SYSTEM)
            }
            if (livingHeritage) add(PlaceBadge.LIVING_HERITAGE)
            if (primary == PlacePrimaryCategory.ARCHAEOLOGICAL_SITE ||
                placeAttributes.contains(PlaceAttribute.ARCHAEOLOGICAL)
            ) {
                add(PlaceBadge.ARCHAEOLOGICAL_SITE)
            }
            if (placeAttributes.contains(PlaceAttribute.HISTORICAL)) {
                add(PlaceBadge.HISTORIC_MONUMENT)
            }
            if (primary == PlacePrimaryCategory.NATURAL_HERITAGE) {
                add(PlaceBadge.NATURAL_HERITAGE)
            }
        }
        return result.distinct().take(limit)
    }

    /** District and province, in that order, when either is recorded. */
    val regionLabel: String
        get() = listOf(district, province)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .joinToString(" • ")

    /**
     * The short line a comparison card shows. Significance is editorial context
     * written for exactly this purpose; description is the fallback for older
     * documents that predate the field.
     */
    val comparisonLine: String
        get() = significance.trim().ifBlank { description.trim() }

    val isUnescoListed: Boolean
        get() = unescoStatus.contains("world heritage", ignoreCase = true) ||
            tags.any { it.contains("UNESCO", ignoreCase = true) }
}

/**
 * Case-folds, turns stored key separators (`PAGODA_NEWA`) into spaces and
 * collapses runs of whitespace, so "  newa  pagoda" and "PAGODA_NEWA"-style
 * keys compare on the same footing.
 */
internal fun normalizeSearchText(value: String): String =
    value.replace('_', ' ').trim().lowercase().replace(Whitespace, " ")

private val Whitespace = Regex("\\s+")
