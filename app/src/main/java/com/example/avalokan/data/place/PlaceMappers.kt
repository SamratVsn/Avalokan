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
)
