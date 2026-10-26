package com.example.avalokan.ui.theme

import androidx.compose.ui.graphics.Color

fun discoverCategoryAccent(category: String): Color = when (category) {
    "Historical" -> HistoricalAccent
    "Spiritual", "Cultural" -> CulturalAccent
    "Nature" -> NatureAccent
    "Adventure" -> SandDark
    else -> PrimaryGreen
}

fun discoverCategoryOnAccent(category: String): Color = when (category) {
    "Adventure" -> TextPrimary
    else -> OnPrimary
}
