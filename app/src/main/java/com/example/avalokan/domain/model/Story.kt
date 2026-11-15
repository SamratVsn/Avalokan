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

import com.example.avalokan.domain.classification.HeritageTypology
import com.example.avalokan.domain.classification.StoryCategory

//An editorial story as the UI understands it.
data class Story(
    val id: String,
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
    // Places referenced by this story.
    val placeIds: List<String> = emptyList(),
    // Editorial classification, independent from place/event categories.
    val primaryCategory: String = "",
    val secondaryCategories: List<String> = emptyList(),
    val typologies: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val relatedEventIds: List<String> = emptyList(),
    // Only populated when a historical period is actually verified.
    val period: String = "",
) {
    val primary: StoryCategory?
        get() = StoryCategory.fromKey(primaryCategory)

    val heritageTypologies: List<HeritageTypology>
        get() = HeritageTypology.fromKeys(typologies)
}
