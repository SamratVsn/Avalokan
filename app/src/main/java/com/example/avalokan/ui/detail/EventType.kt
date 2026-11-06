package com.example.avalokan.ui.detail

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class EventType {
    CULTURAL,
    RELIGIOUS,
    TECH;

    companion object {
        fun from(value: String?): EventType = when (value?.uppercase()) {
            "TECH" -> TECH
            "RELIGIOUS" -> RELIGIOUS
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
