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
import com.example.avalokan.domain.classification.EventKind
import com.example.avalokan.domain.classification.GeographicScope
import com.example.avalokan.domain.classification.HeritageGroup
import com.example.avalokan.domain.classification.Participation
import com.example.avalokan.domain.classification.PlaceBadge
import com.example.avalokan.domain.classification.PricingModel
import com.example.avalokan.domain.classification.StoryCategory
import com.example.avalokan.domain.model.EventAboutTitle
import com.example.avalokan.domain.model.EventCta
import com.example.avalokan.domain.model.EventPriceLabel
import com.example.avalokan.domain.model.Place

/**
 * Localisation boundary for classification labels. Domain and data layers
 * carry stable keys only; every user-visible word is resolved here.
 */

@StringRes
fun heritageFilterLabelRes(key: String): Int = when (HeritageGroup.fromKey(key)) {
    HeritageGroup.PAGODA_NEWA -> R.string.heritage_pagoda_newa
    HeritageGroup.STUPA_MONASTERY -> R.string.heritage_stupa_monastery
    HeritageGroup.SHIKHARA_GRANTHAKUTA -> R.string.heritage_shikhara_granthakuta
    HeritageGroup.SACRED_WATER -> R.string.heritage_sacred_water
    HeritageGroup.SACRED_LANDSCAPE -> R.string.heritage_sacred_landscape
    null -> R.string.filter_all
}

@Composable
fun heritageFilterLabel(key: String): String = stringResource(heritageFilterLabelRes(key))

/** A short, educational explanation shown under the selected Discover filter. */
@StringRes
fun heritageFilterHelpRes(key: String): Int? = when (HeritageGroup.fromKey(key)) {
    HeritageGroup.PAGODA_NEWA -> R.string.heritage_pagoda_newa_help
    HeritageGroup.STUPA_MONASTERY -> R.string.heritage_stupa_monastery_help
    HeritageGroup.SHIKHARA_GRANTHAKUTA -> R.string.heritage_shikhara_granthakuta_help
    HeritageGroup.SACRED_WATER -> R.string.heritage_sacred_water_help
    HeritageGroup.SACRED_LANDSCAPE -> R.string.heritage_sacred_landscape_help
    null -> null
}

@StringRes
fun placeBadgeLabelRes(badge: PlaceBadge): Int = when (badge) {
    PlaceBadge.UNESCO_HERITAGE -> R.string.badge_unesco_heritage
    PlaceBadge.BUDDHIST_HERITAGE -> R.string.badge_buddhist_heritage
    PlaceBadge.NEWA_ARCHITECTURE -> R.string.badge_newa_architecture
    PlaceBadge.SACRED_WATER_SYSTEM -> R.string.badge_sacred_water_system
    PlaceBadge.LIVING_HERITAGE -> R.string.badge_living_heritage
    PlaceBadge.ARCHAEOLOGICAL_SITE -> R.string.badge_archaeological_site
    PlaceBadge.HISTORIC_MONUMENT -> R.string.badge_historic_monument
    PlaceBadge.NATURAL_HERITAGE -> R.string.badge_natural_heritage
}

/**
 * Compact badge for any place card: the first verified classification badge,
 * falling back to the legacy editorial label when nothing is classified yet.
 */
@Composable
fun placeBadgeLabel(place: Place): String? =
    place.badges().firstOrNull()?.let { stringResource(placeBadgeLabelRes(it)) }
        ?: place.badge.takeIf { it.isNotBlank() }

@StringRes
fun eventKindLabelRes(kind: EventKind): Int = when (kind) {
    EventKind.FESTIVAL -> R.string.event_kind_festival
    EventKind.RELIGIOUS_CELEBRATION -> R.string.event_kind_religious_celebration
    EventKind.LOCAL_TRADITION -> R.string.event_kind_local_tradition
    EventKind.CULTURAL_PERFORMANCE -> R.string.event_kind_cultural_performance
    EventKind.COMMUNITY_GATHERING -> R.string.event_kind_community_gathering
    EventKind.WORKSHOP -> R.string.event_kind_workshop
    EventKind.EXHIBITION -> R.string.event_kind_exhibition
    EventKind.FOOD_CULINARY -> R.string.event_kind_food_culinary
    EventKind.HERITAGE_EXPERIENCE -> R.string.event_kind_heritage_experience
    EventKind.TECHNOLOGY_INNOVATION -> R.string.event_kind_technology
    EventKind.EDUCATIONAL -> R.string.event_kind_educational
    EventKind.OTHER -> R.string.event_kind_other
}

@StringRes
fun scopeLabelRes(scope: GeographicScope): Int = when (scope) {
    GeographicScope.LOCAL -> R.string.scope_local
    GeographicScope.REGIONAL -> R.string.scope_regional
    GeographicScope.NATIONWIDE -> R.string.scope_nationwide
    GeographicScope.UNKNOWN -> R.string.scope_unknown
}

@StringRes
fun participationLabelRes(participation: Participation): Int = when (participation) {
    Participation.OPEN -> R.string.participation_open
    Participation.REGISTRATION_REQUIRED -> R.string.participation_registration
    Participation.INVITATION_ONLY -> R.string.participation_invitation
    Participation.UNKNOWN -> R.string.participation_unknown
}

@StringRes
fun aboutTitleRes(title: EventAboutTitle): Int = when (title) {
    EventAboutTitle.FESTIVAL -> R.string.about_festival
    EventAboutTitle.CELEBRATION -> R.string.about_celebration
    EventAboutTitle.WORKSHOP -> R.string.about_workshop
    EventAboutTitle.EXHIBITION -> R.string.about_exhibition
    EventAboutTitle.GATHERING -> R.string.about_gathering
    EventAboutTitle.CONFERENCE -> R.string.about_conference
    EventAboutTitle.EVENT -> R.string.about_event
}

@StringRes
fun priceLabelRes(label: EventPriceLabel): Int = when (label) {
    EventPriceLabel.ENTRY -> R.string.price_entry
    EventPriceLabel.TICKET -> R.string.price_ticket
    EventPriceLabel.PRICE_PER_PERSON -> R.string.price_per_person
    EventPriceLabel.DONATION -> R.string.price_donation
}

@StringRes
fun ctaLabelRes(cta: EventCta): Int? = when (cta) {
    EventCta.BOOK_TICKETS -> R.string.cta_book_tickets
    EventCta.REGISTER -> R.string.cta_register
    EventCta.VISIT_WEBSITE -> R.string.cta_visit_event_website
    EventCta.NONE -> null
}

/** Short action label used on compact event cards. */
@StringRes
fun cardActionLabelRes(cta: EventCta): Int? = when (cta) {
    EventCta.BOOK_TICKETS -> R.string.action_tickets
    EventCta.REGISTER -> R.string.action_register
    EventCta.VISIT_WEBSITE -> R.string.action_website
    EventCta.NONE -> null
}

@StringRes
fun storyCategoryLabelRes(category: StoryCategory): Int = when (category) {
    StoryCategory.HISTORY_CIVILIZATION -> R.string.story_category_history
    StoryCategory.ARCHITECTURE_HERITAGE -> R.string.story_category_architecture
    StoryCategory.RELIGION_PHILOSOPHY -> R.string.story_category_religion
    StoryCategory.FESTIVALS_TRADITIONS -> R.string.story_category_festivals
    StoryCategory.ART_CRAFTSMANSHIP -> R.string.story_category_art
    StoryCategory.PEOPLE_LEGENDS -> R.string.story_category_people
    StoryCategory.FOOD_CULTURE -> R.string.story_category_food
    StoryCategory.NATURE_SACRED_LANDSCAPES -> R.string.story_category_nature
    StoryCategory.LOCAL_COMMUNITIES -> R.string.story_category_communities
    StoryCategory.ARCHAEOLOGY -> R.string.story_category_archaeology
}

/** Kept for callers that only need to know whether pricing is verified. */
fun PricingModel.isVerified(): Boolean = this != PricingModel.UNKNOWN

@Composable
fun eventPriceValue(label: EventPriceLabel, value: String): String = when (label) {
    EventPriceLabel.ENTRY -> stringResource(R.string.price_free_value)
    EventPriceLabel.DONATION -> if (value.equals("Donation based", ignoreCase = true)) {
        stringResource(R.string.price_donation_value)
    } else {
        value
    }
    else -> value
}
