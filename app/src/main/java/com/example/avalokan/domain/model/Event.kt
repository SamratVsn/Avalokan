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

import com.example.avalokan.domain.classification.EventFormat
import com.example.avalokan.domain.classification.EventKind
import com.example.avalokan.domain.classification.GeographicScope
import com.example.avalokan.domain.classification.Participation
import com.example.avalokan.domain.classification.PricingModel

//A heritage event as the UI understands it.
data class Event(
    val id: String,
    val title: String,
    val description: String,
    val meta: String = "",
    val fee: String = "",
    val action: String = "Join >",
    val type: String = "CULTURAL",
    val date: String = "",
    val imageUrl: String? = null,
    val featured: Boolean = false,
    val joinLink: String = "",
    // Classification. Empty values mean "not classified", not a guessed type.
    val eventKind: String = "",
    // Geographical reach of the event, not its venue. Stored as a stable key.
    val locality: String = "",
    // Pricing model: FREE / TICKETED / DONATION / UNKNOWN.
    val ticketing: String = "",
    // How the fee is charged (PER_PERSON, PER_GROUP, ...). Blank = unknown.
    val priceBasis: String = "",
    val participation: String = "",
    val format: String = "",
    val secondaryCategories: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    // Optional links to related heritage content.
    val placeId: String = "",
    val storyId: String = "",
    val relatedPlaceIds: List<String> = emptyList(),
    val relatedEventIds: List<String> = emptyList(),
    // Provenance. Blank means "not recorded", never "verified".
    val sourceLabel: String = "",
    val sourceUrl: String = "",
    val lastVerifiedAt: String = "",
    val verificationStatus: String = "",
) {
    val primaryKind: EventKind
        get() = EventKind.fromKey(eventKind) ?: EventKind.fromLegacyType(type)

    val geographicScope: GeographicScope
        get() = GeographicScope.fromKey(locality)

    val participationRequirement: Participation
        get() = Participation.fromKey(participation)

    val eventFormat: EventFormat?
        get() = EventFormat.fromKey(format)

    val pricing: PricingModel
        get() = PricingModel.fromKey(ticketing)

    /**
     * Featured presentation is only valid for explicitly classified festival
     * content. A technology event (or any unclassified event) must never gain
     * the featured treatment just because `featured` is true.
     */
    val isFeaturedEligible: Boolean
        get() = featured && primaryKind.isFestivalLike

    val hasValidJoinLink: Boolean
        get() = joinLink.trim().startsWith("http://", ignoreCase = true) ||
            joinLink.trim().startsWith("https://", ignoreCase = true)
}
