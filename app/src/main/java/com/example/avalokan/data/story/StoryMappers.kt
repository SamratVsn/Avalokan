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

package com.example.avalokan.data.story

import com.example.avalokan.domain.model.Story

/**
 * Boundary between the Room row and the UI-facing model. Keep field renames or
 * schema changes here so screens never see the persistence shape.
 */
internal fun StoryItem.toDomain(): Story = Story(
    id = id,
    title = title,
    subtitle = subtitle,
    description = description,
    imageUrl = imageUrl,
    author = author,
    date = date,
    readTime = readTime,
    badge = badge,
    curator = curator,
    sourceLabel = sourceLabel,
    sourceUrl = sourceUrl,
    lastVerifiedAt = lastVerifiedAt,
    verificationStatus = verificationStatus,
    placeIds = placeIds,
    primaryCategory = primaryCategory,
    secondaryCategories = secondaryCategories,
    typologies = typologies,
    tags = tags,
    relatedEventIds = relatedEventIds,
    period = period,
)
