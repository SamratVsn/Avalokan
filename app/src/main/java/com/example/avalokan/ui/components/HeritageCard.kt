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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.avalokan.R
import com.example.avalokan.ui.theme.BadgeShape

@Composable
fun HeritageCard(
    title: String,
    subtitle: String,
    badge: String? = null,
    rating: String? = null,
    /** Citation for [rating]. A rating without one is never rendered. */
    ratingSource: String? = null,
    description: String? = null,
    descriptionLines: Int = 3,
    subtitleStyle: TextStyle? = null,
    imageUrl: String? = null,
    imageCaption: String? = null,
    imageCredit: String? = null,
    cardWidth: Dp? = null,
    imageHeight: Dp = 110.dp,
    imageCorner: Dp = 20.dp,
    showSave: Boolean = false,
    isSaved: Boolean = false,
    onSaveClick: (() -> Unit)? = null,
    onCardClick: (() -> Unit)? = null,
    cardContainer: Boolean = false,
    modifier: Modifier = Modifier
) {
    val widthMod = if (cardWidth != null) Modifier.width(cardWidth) else Modifier
    val clickMod = if (onCardClick != null) {
        Modifier.clickable(role = Role.Button, onClick = onCardClick)
    } else {
        Modifier
    }
    // Unsourced ratings are hidden rather than presented as a quality signal.
    val shownRating = rating?.takeIf { it.isNotBlank() && !ratingSource.isNullOrBlank() }
    if (cardContainer) {
        Card(
            modifier = modifier.fillMaxWidth().then(clickMod),
            shape = RoundedCornerShape(imageCorner),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column {
                HeritageImageBlock(
                    imageHeight = imageHeight,
                    title = title,
                    imageUrl = imageUrl,
                    imageCaption = imageCaption,
                    imageCredit = imageCredit,
                    showSave = showSave,
                    isSaved = isSaved,
                    onSaveClick = onSaveClick
                )
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    HeritageTexts(
                        title = title,
                        subtitle = subtitle,
                        subtitleStyle = subtitleStyle,
                        badge = badge,
                        rating = shownRating,
                        description = description,
                        descriptionLines = descriptionLines
                    )
                }
            }
        }
    } else {
        Column(
            modifier = modifier.then(widthMod).then(clickMod),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Card(shape = RoundedCornerShape(imageCorner)) {
                HeritageImageBlock(
                    imageHeight = imageHeight,
                    title = title,
                    imageUrl = imageUrl,
                    imageCaption = imageCaption,
                    imageCredit = imageCredit,
                    showSave = showSave,
                    isSaved = isSaved,
                    onSaveClick = onSaveClick
                )
            }
            HeritageTexts(
                title = title,
                subtitle = subtitle,
                subtitleStyle = subtitleStyle,
                badge = badge,
                rating = shownRating,
                description = description,
                descriptionLines = descriptionLines
            )
        }
    }
}

@Composable
private fun HeritageImageBlock(
    imageHeight: Dp,
    title: String,
    imageUrl: String?,
    imageCaption: String?,
    imageCredit: String?,
    showSave: Boolean,
    isSaved: Boolean,
    onSaveClick: (() -> Unit)?
) {
    // The container reserves the image height before Coil resolves, so the card
    // never grows or jumps when the photo arrives.
    Box(modifier = Modifier.fillMaxWidth().height(imageHeight)) {
        HeritageImage(
            imageUrl = imageUrl,
            contentDescription = title,
            caption = imageCaption,
            credit = imageCredit,
            modifier = Modifier.fillMaxSize()
        )
        if (showSave && onSaveClick != null) {
            PhotoOverlayIconButton(
                icon = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                contentDescription = stringResource(
                    if (isSaved) R.string.remove_saved_place else R.string.save_place,
                    title
                ),
                onClick = onSaveClick,
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                buttonSize = 28.dp,
                iconSize = 16.dp
            )
        }
    }
}

@Composable
private fun HeritageTexts(
    title: String,
    subtitle: String,
    subtitleStyle: TextStyle?,
    badge: String?,
    rating: String?,
    description: String?,
    descriptionLines: Int
) {
    Text(
        text = title.ifBlank { stringResource(R.string.untitled) },
        style = MaterialTheme.typography.titleMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
    Text(
        text = subtitle,
        style = subtitleStyle ?: MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
    if (!description.isNullOrBlank()) {
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = descriptionLines,
            overflow = TextOverflow.Ellipsis
        )
    }
    if (!badge.isNullOrBlank() || !rating.isNullOrBlank()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!badge.isNullOrBlank()) {
                Surface(
                    shape = BadgeShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
            if (!rating.isNullOrBlank()) {
                Spacer(Modifier.weight(1f, fill = false))
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Rated $rating out of 5",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = rating,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}
