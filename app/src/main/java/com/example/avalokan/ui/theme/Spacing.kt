package com.example.avalokan.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class AvalokanSpacing(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    // Semantic aliases from spec
    val sidePadding: Dp = 16.dp,
    val sectionVertical: Dp = 24.dp,
    val grid: Dp = 8.dp
)

val Spacing = AvalokanSpacing()
