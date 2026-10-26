package com.example.avalokan.ui.detail

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Palette
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.avalokan.ui.theme.CulturalAccent
import com.example.avalokan.ui.theme.HistoricalAccent
import com.example.avalokan.ui.theme.InfoBlue

enum class EventType(val accent: Color) {
    CULTURAL(CulturalAccent),
    RELIGIOUS(HistoricalAccent),
    TECH(InfoBlue);

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
