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

package com.example.avalokan.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.avalokan.ui.theme.ImageScrimStrong
import com.example.avalokan.ui.theme.OnImage

/**
 * Stable aspect ratios shared by every card type. Using a ratio instead of a
 * hard-coded height keeps the layout identical across screen widths and stops
 * the card from resizing while an image loads.
 */
object HeritageImageRatio {
    /** Wide media card: Discover comparison cards and featured events. */
    const val CARD = 16f / 9f

    /** Full-bleed detail hero. */
    const val HERO = 4f / 3f
}

/**
 * One image surface for the whole app.
 *
 * - The container always paints a neutral surface, so cards reserve their space
 *   before the image resolves and never shift.
 * - Coil crossfades successful loads over that surface instead of flashing a
 *   blank block.
 * - A missing or failed image gets a deliberate icon fallback rather than empty
 *   coloured space.
 * - [caption] and [credit] render only when the caller actually has them, so no
 *   invented photo metadata appears in the UI.
 */
@Composable
fun HeritageImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    fallbackIcon: ImageVector = Icons.Default.Landscape,
    caption: String? = null,
    credit: String? = null
) {
    val label = listOfNotNull(
        caption?.takeIf { it.isNotBlank() },
        credit?.takeIf { it.isNotBlank() }
    ).joinToString(" · ")

    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl.isNullOrBlank()) {
            ImageFallback(icon = fallbackIcon)
        } else {
            val request = imageRequest(imageUrl)
            SubcomposeAsyncImage(
                model = request,
                contentDescription = null,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize(),
                loading = { ImageLoading() },
                success = { state ->
                    Image(
                        painter = state.painter,
                        contentDescription = contentDescription,
                        contentScale = contentScale,
                        modifier = Modifier.fillMaxSize()
                    )
                    if (label.isNotEmpty()) {
                        ImageCaption(label)
                    }
                },
                error = { ImageFallback(icon = fallbackIcon, isError = true) }
            )
        }
    }
}

/**
 * Circular avatar with the same loading contract as [HeritageImage]. The
 * fallback is the user's initial, which is also what a signed-out profile
 * shows, so a failed avatar never collapses into empty space.
 */
@Composable
fun HeritageAvatar(
    imageUrl: String?,
    fallbackText: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    fallbackStyle: TextStyle = MaterialTheme.typography.headlineMedium,
    fallbackColor: Color = MaterialTheme.colorScheme.primary,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer
) {
    Box(
        modifier = modifier.clip(CircleShape).background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl.isNullOrBlank()) {
            AvatarInitial(fallbackText, fallbackStyle, fallbackColor)
        } else {
            SubcomposeAsyncImage(
                model = imageRequest(imageUrl),
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(CircleShape),
                loading = { AvatarInitial(fallbackText, fallbackStyle, fallbackColor) },
                error = { AvatarInitial(fallbackText, fallbackStyle, fallbackColor) }
            )
        }
    }
}

@Composable
private fun imageRequest(url: String): ImageRequest {
    val context = LocalContext.current
    return remember(url) {
        ImageRequest.Builder(context)
            .data(url)
            .crossfade(180)
            .build()
    }
}

@Composable
private fun ImageLoading() {
    Icon(
        Icons.Default.Landscape,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.28f),
        modifier = Modifier.size(32.dp)
    )
}

@Composable
private fun ImageFallback(
    icon: ImageVector,
    isError: Boolean = false
) {
    Icon(
        imageVector = if (isError) Icons.Default.ImageNotSupported else icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
        modifier = Modifier.size(32.dp)
    )
}

@Composable
private fun BoxScope.ImageCaption(label: String) {
    Box(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Color.Transparent, ImageScrimStrong)
                )
            )
            .padding(start = 12.dp, end = 12.dp, top = 20.dp, bottom = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = OnImage,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun AvatarInitial(
    text: String,
    style: TextStyle,
    color: Color
) {
    Text(
        text = text,
        style = style,
        color = color,
        fontWeight = FontWeight.SemiBold
    )
}
