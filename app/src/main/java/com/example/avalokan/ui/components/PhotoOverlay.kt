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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.avalokan.ui.theme.ImageScrim
import com.example.avalokan.ui.theme.OnImage

/** The minimum touch target every icon control must expose. */
val MinimumTouchTarget = 48.dp

/**
 * Icon control that stays legible over arbitrary photography. The 48dp touch
 * target is independent of [buttonSize], so a small visual circle still meets
 * the minimum target without making the photo busier. Scrim and icon use fixed
 * colors because the backdrop is an image, not a themed surface.
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
        modifier = modifier.size(MinimumTouchTarget)
    ) {
        Box(
            modifier = Modifier.size(buttonSize).background(ImageScrim, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = OnImage,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

/**
 * Themed circular icon button used for navigation and utility actions. The
 * visual circle can be smaller than [MinimumTouchTarget]; the clickable box
 * around it is always 48dp so motor-impaired users get a reliable target.
 */
@Composable
fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    containerSize: Dp = 36.dp,
    iconSize: Dp = 18.dp,
    elevation: Dp = 0.dp
) {
    Box(
        modifier = modifier
            .size(MinimumTouchTarget)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(containerSize)
                .shadow(elevation, CircleShape)
                .background(containerColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = contentColor,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}
