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
