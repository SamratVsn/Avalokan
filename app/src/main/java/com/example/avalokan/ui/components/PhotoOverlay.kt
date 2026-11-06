package com.example.avalokan.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.avalokan.ui.theme.ImageScrim
import com.example.avalokan.ui.theme.OnImage

/**
 * Icon control that stays legible over arbitrary photography. The scrim and
 * icon use fixed colors instead of theme roles because the backdrop is an
 * image, not a themed surface.
 */
@Composable
fun PhotoOverlayIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonSize: Dp = 40.dp,
    iconSize: Dp = 18.dp
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(buttonSize)
            .background(ImageScrim, CircleShape)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = OnImage,
            modifier = Modifier.size(iconSize)
        )
    }
}

/**
 * Outlined pill for actions placed directly on an image, such as "View
 * Gallery". A scrim fill and fixed light content keep the label readable over
 * bright and dark photos alike.
 */
@Composable
fun PhotoOverlayOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, OnImage.copy(alpha = 0.72f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = ImageScrim,
            contentColor = OnImage
        )
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
        }
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}
