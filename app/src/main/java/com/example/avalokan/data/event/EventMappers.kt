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
)
