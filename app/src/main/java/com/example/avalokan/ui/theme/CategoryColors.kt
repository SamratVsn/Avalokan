package com.example.avalokan.ui.theme

import androidx.compose.ui.graphics.Color

fun discoverCategoryAccent(category: String): Color = when (category) {
    "PAGODA_NEWA" -> HistoricalAccent
    "STUPA_MONASTERY" -> CulturalAccent
    "SHIKHARA_GRANTHAKUTA" -> PrimaryGreenDark
    "SACRED_WATER" -> NatureAccent
    "SACRED_LANDSCAPE" -> SandDark
    else -> PrimaryGreen
}

fun discoverCategoryOnAccent(category: String): Color = when (category) {
    "SACRED_LANDSCAPE" -> TextPrimary
    else -> OnPrimary
}
