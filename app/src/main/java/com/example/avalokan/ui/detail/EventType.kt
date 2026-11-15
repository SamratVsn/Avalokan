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

package com.example.avalokan.ui.detail

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.avalokan.domain.classification.EventKind

enum class EventType {
    CULTURAL,
    RELIGIOUS,
    TECH;

    companion object {
        /**
         * Maps both the legacy `type` field and the classified event kind.
         * Legacy "Technical" documents previously fell through to CULTURAL,
         * which made AI Conf and DevFest look like cultural festivals.
         */
        fun from(value: String?, kind: EventKind? = null): EventType = when {
            kind == EventKind.TECHNOLOGY_INNOVATION -> TECH
            kind == EventKind.RELIGIOUS_CELEBRATION ||
                kind == EventKind.LOCAL_TRADITION -> RELIGIOUS
            value?.trim()?.uppercase() in setOf("TECH", "TECHNICAL", "TECHNOLOGY") -> TECH
            value?.trim()?.uppercase() == "RELIGIOUS" -> RELIGIOUS
            else -> CULTURAL
        }
    }
}

/**
 * Badge icon per type. Top-level composable because the icon getters require
 * a composer; call it from @Composable content only.
 */
@Composable
fun eventBadgeIcon(type: EventType): ImageVector = when (type) {
    EventType.CULTURAL -> Icons.Default.Palette
    EventType.RELIGIOUS -> Icons.Default.CalendarMonth
    EventType.TECH -> Icons.Default.Memory
}

/**
 * Event accents mapped onto theme roles. The raw brand accents are fixed
 * colors, so using them directly would make a badge or button disappear in
 * dark mode. Mapping each type to a container/on-container pair keeps the
 * type distinction without sacrificing contrast.
 */
@Composable
fun EventType.badgeContainerColor(): Color = when (this) {
    EventType.CULTURAL -> MaterialTheme.colorScheme.tertiaryContainer
    EventType.RELIGIOUS -> MaterialTheme.colorScheme.primaryContainer
    EventType.TECH -> MaterialTheme.colorScheme.secondaryContainer
}

@Composable
fun EventType.badgeContentColor(): Color = when (this) {
    EventType.CULTURAL -> MaterialTheme.colorScheme.onTertiaryContainer
    EventType.RELIGIOUS -> MaterialTheme.colorScheme.onPrimaryContainer
    EventType.TECH -> MaterialTheme.colorScheme.onSecondaryContainer
}

@Composable
fun EventType.actionContainerColor(): Color = when (this) {
    EventType.CULTURAL -> MaterialTheme.colorScheme.tertiary
    EventType.RELIGIOUS -> MaterialTheme.colorScheme.primary
    EventType.TECH -> MaterialTheme.colorScheme.secondary
}

@Composable
fun EventType.actionContentColor(): Color = when (this) {
    EventType.CULTURAL -> MaterialTheme.colorScheme.onTertiary
    EventType.RELIGIOUS -> MaterialTheme.colorScheme.onPrimary
    EventType.TECH -> MaterialTheme.colorScheme.onSecondary
}
