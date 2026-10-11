package com.example.avalokan.ui.theme

import androidx.compose.ui.graphics.Color

// Himalayan Fresh — brand tokens
val PrimaryTeal = Color(0xFF00796B)
val PrimaryLight = Color(0xFFE0F2F1)
val AccentMarigold = Color(0xFFFF6F00)

val TextPrimary = Color(0xFF333333)
val TextSecondary = Color(0xFF757575)

val AppBackground = Color(0xFFF5F5F5)
val AppSurface = Color(0xFFFFFFFF)

// Supporting on-colors
val OnPrimary = Color(0xFFFFFFFF)
val OnAccent = Color(0xFFFFFFFF)

// --- Derived system tokens (centralized; no raw hexes in screens) ---

/** Darker teal for gradients ending below PrimaryTeal. */
val TealDark = Color(0xFF00564B)

/** Softer mint for promotional cards. */
val MintSoft = Color(0xFFF0F9F8)

/** Light grey fills (search bars, gear backgrounds). */
val GreyLight = Color(0xFFF5F5F5)

/** Hairline borders on surface cards and chips. */
val GreyBorder = Color(0xFFEEEEEE)

/** Muted card fill for stats and experience cards. */
val SurfaceMuted = Color(0xFFF9F9F9)

/** Editorial body grey (softer than pure black). */
val StoryBody = Color(0xFF444444)

/** Deep blue accent reserved for tech events. */
val TechBlue = Color(0xFF1D4ED8)

/** Danger / feedback accents. */
val DangerRed = Color(0xFFE53935)
val DangerBg = Color(0xFFFFEBEE)
val WarmBg = Color(0xFFFFF3E0)
val NeutralBg = Color(0xFFF5F5F5)

/** Scrim behind hero overlay buttons (legibility on any image). */
val HeroScrim = Color.Black.copy(alpha = 0.2f)
