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

package com.example.avalokan.data.event

import com.example.avalokan.domain.model.Event

/**
 * Boundary between the Room row and the UI-facing model. Keep field renames or
 * schema changes here so screens never see the persistence shape.
 */
internal fun EventItem.toDomain(): Event = Event(
    id = id,
    title = title,
    description = description,
    meta = meta,
    fee = fee,
    action = action,
    type = type,
    date = date,
    imageUrl = imageUrl,
    featured = featured,
    joinLink = joinLink,
    eventKind = eventKind,
    locality = locality,
    ticketing = ticketing,
    priceBasis = priceBasis,
    participation = participation,
    format = format,
    secondaryCategories = secondaryCategories,
    tags = tags,
    placeId = placeId,
    storyId = storyId,
    relatedPlaceIds = relatedPlaceIds,
    relatedEventIds = relatedEventIds,
    sourceLabel = sourceLabel,
    sourceUrl = sourceUrl,
    lastVerifiedAt = lastVerifiedAt,
    verificationStatus = verificationStatus,
)
