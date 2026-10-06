package com.example.avalokan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.avalokan.ui.theme.AccentMarigold
import com.example.avalokan.ui.theme.BadgeShape
import com.example.avalokan.ui.theme.PrimaryLight
import com.example.avalokan.ui.theme.PrimaryTeal
import com.example.avalokan.ui.theme.StandardCardShape

@Composable
fun HeritageCard(
    title: String,
    subtitle: String,
    badge: String? = null,
    rating: String? = null,
    description: String? = null,
    descriptionLines: Int = 3,
    imageUrl: String? = null,
    cardWidth: Dp? = null,
    imageHeight: Dp = 110.dp,
    showSave: Boolean = false,
    isSaved: Boolean = false,
    onSaveClick: (() -> Unit)? = null,
    onCardClick: (() -> Unit)? = null,
    cardContainer: Boolean = false,
    modifier: Modifier = Modifier
) {
    val widthMod = if (cardWidth != null) Modifier.width(cardWidth) else Modifier
    val clickMod = if (onCardClick != null) Modifier.clickable(onClick = onCardClick) else Modifier
    if (cardContainer) {
        Card(
            modifier = modifier.fillMaxWidth().then(clickMod),
            shape = StandardCardShape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column {
                HeritageImage(
                    imageHeight = imageHeight,
                    title = title,
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
                        badge = badge,
                        rating = rating,
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
            Card(shape = StandardCardShape) {
                HeritageImage(
                    imageHeight = imageHeight,
                    title = title,
                    showSave = showSave,
                    isSaved = isSaved,
                    onSaveClick = onSaveClick
                )
            }
            HeritageTexts(
                title = title,
                subtitle = subtitle,
                badge = badge,
                rating = rating,
                description = description,
                descriptionLines = descriptionLines
            )
        }
    }
}

@Composable
private fun HeritageImage(
    imageHeight: Dp,
    title: String,
    showSave: Boolean,
    isSaved: Boolean,
    onSaveClick: (() -> Unit)?
) {
    // Image container (reference size/shape); AsyncImage fills it when data lands
    Box(
        modifier = Modifier.fillMaxWidth().height(imageHeight)
            .background(PrimaryLight),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Landscape,
            contentDescription = null,
            tint = PrimaryTeal
        )
        if (showSave && onSaveClick != null) {
            Surface(
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.9f)
            ) {
                IconButton(onClick = onSaveClick, modifier = Modifier.size(28.dp)) {
                    Icon(
                        if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = if (isSaved) "Remove $title from collection"
                        else "Save $title to collection",
                        tint = PrimaryTeal,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HeritageTexts(
    title: String,
    subtitle: String,
    badge: String?,
    rating: String?,
    description: String?,
    descriptionLines: Int
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
    Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
    if (description != null) {
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = descriptionLines,
            overflow = TextOverflow.Ellipsis
        )
    }
    if (badge != null || rating != null) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (badge != null) {
                Surface(
                    shape = BadgeShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            if (rating != null) {
                Spacer(Modifier.weight(1f, fill = false))
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Rated $rating out of 5",
                    tint = AccentMarigold,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = rating,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = AccentMarigold
                )
            }
        }
    }
}
