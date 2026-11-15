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

import com.example.avalokan.domain.model.Place

/**
 * Boundary between the Room row and the UI-facing model. Keep field renames or
 * schema changes here so screens never see the persistence shape.
 */
internal fun PlaceItem.toDomain(): Place = Place(
    id = id,
    name = name,
    description = description,
    meta = meta,
    category = category,
    rating = rating,
    entryFee = entryFee,
    bestTime = bestTime,
    hours = hours,
    badge = badge,
    featured = featured,
    imageUrl = imageUrl,
    province = province,
    district = district,
    latitude = latitude,
    longitude = longitude,
    era = era,
    unescoStatus = unescoStatus,
    heritageType = heritageType,
    significance = significance,
    livingHeritage = livingHeritage,
    etiquette = etiquette,
    accessibility = accessibility,
    primaryCategory = primaryCategory,
    secondaryCategories = secondaryCategories,
    typologies = typologies,
    attributes = attributes,
    tags = tags,
    locality = locality,
    relatedPlaceIds = relatedPlaceIds,
    relatedEventIds = relatedEventIds,
    sourceLabel = sourceLabel,
    sourceUrl = sourceUrl,
    lastVerifiedAt = lastVerifiedAt,
    verificationStatus = verificationStatus,
)
