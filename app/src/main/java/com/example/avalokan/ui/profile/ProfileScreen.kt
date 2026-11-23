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

package com.example.avalokan.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.avalokan.R
import com.example.avalokan.domain.model.Event
import com.example.avalokan.domain.model.Place
import com.example.avalokan.ui.components.CircleIconButton
import com.example.avalokan.ui.components.HeritageAvatar
import com.example.avalokan.ui.components.HeritageImage
import com.example.avalokan.ui.components.PlaceComparisonCard
import com.example.avalokan.ui.theme.AvalokanTheme
import com.example.avalokan.ui.theme.Spacing
import com.example.avalokan.ui.theme.StandardCardShape

/** Width of a place card inside a horizontal Profile shelf. */
private val ShelfCardWidth = 272.dp

/**
 * The user's personal exploration space: identity, then everything they chose
 * to keep — saved places, places they marked as visited, favorite events —
 * each linking back to its detail screen. Every count is derived from the same
 * lists shown below it. No social or public-profile features.
 */
@Composable
fun ProfileScreen(
    onSettingsClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onSeeAllSavedClick: () -> Unit = {},
    onPlaceClick: (String) -> Unit = {},
    onEventClick: (String) -> Unit = {},
    onDiscoverClick: () -> Unit = {},
    onEventsClick: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val userName by viewModel.userName.collectAsState()
    val bio by viewModel.bio.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val avatarUri by viewModel.avatarUri.collectAsState()
    val savedPlaces by viewModel.savedPlaces.collectAsState()
    val savedIds by viewModel.savedIds.collectAsState()
    val visitedPlaces by viewModel.visitedPlaces.collectAsState()
    val favoriteEvents by viewModel.favoriteEvents.collectAsState()

    ProfileContent(
        userName = userName,
        bio = bio,
        stats = stats,
        avatarUri = avatarUri,
        savedPlaces = savedPlaces,
        savedIds = savedIds,
        visitedPlaces = visitedPlaces,
        favoriteEvents = favoriteEvents,
        onSettingsClick = onSettingsClick,
        onEditProfileClick = onEditProfileClick,
        onSeeAllSavedClick = onSeeAllSavedClick,
        onPlaceClick = onPlaceClick,
        onToggleSave = viewModel::toggleSave,
        onEventClick = onEventClick,
        onDiscoverClick = onDiscoverClick,
        onEventsClick = onEventsClick
    )
}

@Composable
private fun ProfileContent(
    userName: String,
    bio: String,
    stats: ProfileStats,
    avatarUri: String?,
    savedPlaces: List<Place>,
    savedIds: Set<String>,
    visitedPlaces: List<Place>,
    favoriteEvents: List<Event>,
    onSettingsClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onSeeAllSavedClick: () -> Unit,
    onPlaceClick: (String) -> Unit,
    onToggleSave: (String) -> Unit,
    onEventClick: (String) -> Unit,
    onDiscoverClick: () -> Unit,
    onEventsClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = Spacing.extraLarge),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(Modifier.height(Spacing.small))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.profile),
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                fontWeight = FontWeight.Bold
            )
            CircleIconButton(
                icon = Icons.Default.Settings,
                contentDescription = stringResource(R.string.settings),
                onClick = onSettingsClick,
                containerSize = 40.dp,
                iconSize = 18.dp
            )
        }
        UserSection(
            userName = userName,
            bio = bio,
            stats = stats,
            avatarUri = avatarUri,
            onEditProfileClick = onEditProfileClick,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        PlaceShelf(
            title = stringResource(R.string.savedPlaces),
            places = savedPlaces,
            savedIds = savedIds,
            onSeeAllClick = onSeeAllSavedClick,
            onPlaceClick = onPlaceClick,
            onToggleSave = onToggleSave,
            emptyIcon = Icons.Default.BookmarkBorder,
            emptyTitle = stringResource(R.string.saved_places_empty),
            emptyMessage = stringResource(R.string.saved_places_empty_hint),
            onEmptyAction = onDiscoverClick
        )
        PlaceShelf(
            title = stringResource(R.string.visitedPlaces),
            places = visitedPlaces,
            savedIds = savedIds,
            onSeeAllClick = null,
            onPlaceClick = onPlaceClick,
            onToggleSave = onToggleSave,
            emptyIcon = Icons.Default.Place,
            emptyTitle = stringResource(R.string.visited_places_empty),
            emptyMessage = stringResource(R.string.visited_places_empty_hint),
            onEmptyAction = onDiscoverClick
        )
        FavoriteEvents(
            favoriteEvents = favoriteEvents,
            onEventClick = onEventClick,
            onEventsClick = onEventsClick,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        SettingsEntry(
            onClick = onSettingsClick,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
private fun UserSection(
    userName: String,
    bio: String,
    stats: ProfileStats,
    avatarUri: String?,
    onEditProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(40.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                HeritageAvatar(
                    imageUrl = avatarUri,
                    fallbackText = userName.firstOrNull()?.uppercase() ?: "G",
                    contentDescription = stringResource(R.string.profile_photo),
                    modifier = Modifier.size(72.dp),
                    containerColor = MaterialTheme.colorScheme.surface
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = userName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = bio.ifBlank { stringResource(R.string.no_bio) },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f)
            )
            Spacer(Modifier.height(12.dp))
            FilledTonalButton(onClick = onEditProfileClick) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.editProfile))
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                UserSectionInfo(stats.visited, stringResource(R.string.profile_stat_visited))
                UserSectionInfo(stats.saved, stringResource(R.string.profile_stat_saved))
                UserSectionInfo(stats.favorites, stringResource(R.string.profile_stat_favorites))
            }
        }
    }
}

@Composable
private fun UserSectionInfo(number: Int, text: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$number",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.72f)
        )
    }
}

/**
 * A titled, horizontally scrolling shelf of every place in [places], using the
 * same [PlaceComparisonCard] as Discover and the category listing.
 */
@Composable
private fun PlaceShelf(
    title: String,
    places: List<Place>,
    savedIds: Set<String>,
    onSeeAllClick: (() -> Unit)?,
    onPlaceClick: (String) -> Unit,
    onToggleSave: (String) -> Unit,
    emptyIcon: ImageVector,
    emptyTitle: String,
    emptyMessage: String,
    onEmptyAction: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(
            title = title,
            count = places.size,
            actionLabel = stringResource(R.string.manage),
            onAction = onSeeAllClick?.takeIf { places.isNotEmpty() },
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        if (places.isEmpty()) {
            EmptyCollectionCard(
                icon = emptyIcon,
                title = emptyTitle,
                message = emptyMessage,
                actionLabel = stringResource(R.string.discover_places),
                onAction = onEmptyAction,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(places, key = { it.id }) { place ->
                    PlaceComparisonCard(
                        place = place,
                        isSaved = place.id in savedIds,
                        onSaveClick = { onToggleSave(place.id) },
                        onClick = { onPlaceClick(place.id) },
                        modifier = Modifier.width(ShelfCardWidth).animateItem()
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    count: Int,
    actionLabel: String,
    onAction: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().height(40.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            if (count > 0) {
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (onAction != null) {
            TextButton(onClick = onAction) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FavoriteEvents(
    favoriteEvents: List<Event>,
    onEventClick: (String) -> Unit,
    onEventsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(
            title = stringResource(R.string.favoriteEvents),
            count = favoriteEvents.size,
            actionLabel = stringResource(R.string.browse_events),
            onAction = onEventsClick.takeIf { favoriteEvents.isNotEmpty() }
        )
        if (favoriteEvents.isEmpty()) {
            EmptyCollectionCard(
                icon = Icons.Default.CalendarMonth,
                title = stringResource(R.string.favorite_events_empty_title),
                message = stringResource(R.string.favorite_events_empty_hint),
                actionLabel = stringResource(R.string.browse_events),
                onAction = onEventsClick
            )
        } else {
            favoriteEvents.forEach { event ->
                FavoriteEventRow(event = event, onClick = { onEventClick(event.id) })
            }
        }
    }
}

@Composable
private fun FavoriteEventRow(event: Event, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = StandardCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                HeritageImage(
                    imageUrl = event.imageUrl,
                    contentDescription = event.title,
                    fallbackIcon = Icons.Default.CalendarMonth,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = event.title.ifBlank { stringResource(R.string.untitled) },
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = event.meta.substringBefore("•").trim()
                        .ifBlank { stringResource(R.string.no_location) },
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = event.date.ifBlank { stringResource(R.string.no_date) },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsEntry(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = StandardCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = stringResource(R.string.settings),
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.profile_settings_hint),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyCollectionCard(
    icon: ImageVector,
    title: String,
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onAction,
        modifier = modifier.fillMaxWidth(),
        shape = StandardCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Preview(name = "Profile — Light")
@Preview(
    name = "Profile — Dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ProfilePreview() {
    AvalokanTheme {
        ProfileContent(
            userName = "Guest",
            bio = "Exploring the heritage of Nepal",
            stats = ProfileStats(),
            avatarUri = null,
            savedPlaces = emptyList(),
            savedIds = emptySet(),
            visitedPlaces = emptyList(),
            favoriteEvents = emptyList(),
            onSettingsClick = {},
            onEditProfileClick = {},
            onSeeAllSavedClick = {},
            onPlaceClick = {},
            onToggleSave = {},
            onEventClick = {},
            onDiscoverClick = {},
            onEventsClick = {}
        )
    }
}
