package com.example.avalokan.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AvalokanShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

// Aliases for editorial usage
val EditorialCardShape = RoundedCornerShape(28.dp)
val EditorialLargeCardShape = RoundedCornerShape(32.dp)
val StandardCardShape = RoundedCornerShape(20.dp)
val StandardButtonShape = RoundedCornerShape(16.dp)
val BadgeShape = CircleShape
