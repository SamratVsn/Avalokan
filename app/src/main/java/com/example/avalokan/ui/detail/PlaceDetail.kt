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

package com.example.avalokan.ui.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.avalokan.R
import com.example.avalokan.domain.classification.PlaceBadge
import com.example.avalokan.domain.model.Place
import com.example.avalokan.domain.model.Story
import com.example.avalokan.ui.common.dataOrNull
import com.example.avalokan.ui.common.placeBadgeLabelRes
import com.example.avalokan.ui.components.HeritageImage
import com.example.avalokan.ui.components.PhotoOverlayIconButton
import com.example.avalokan.ui.theme.AvalokanTheme
import com.example.avalokan.ui.theme.BadgeShape
import com.example.avalokan.ui.theme.StandardCardShape
import kotlinx.coroutines.launch
import java.util.Locale

private data class PracticalRow(
    val icon: ImageVector,
    val label: String,
    val value: String
)

/**
 * The place page leads with why the place matters, then verified practical
 * information, living-heritage guidance, location and connected content.
 *
 * Two variants are driven by data, not by separate screens: a living site
 * (`livingHeritage`) gets community-first language, a respectful-visit block
 * and "Visitor information"; a monument keeps "Practical information".
 */
@Composable
fun PlaceDetailScreen(
    placeId: String = "preview",
    onBackClick: () -> Unit = {},
    onPlaceClick: (String) -> Unit = {},
    onStoryClick: (String) -> Unit = {},
    viewModel: PlaceDetailViewModel = hiltViewModel()
) {
    val placeState by viewModel.placeState.collectAsState()
    val relatedContent by viewModel.relatedContent.collectAsState()
    val savedIds by viewModel.savedIds.collectAsState()
    val visitedIds by viewModel.visitedIds.collectAsState()
    val context = LocalContext.current
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val savedMessage = stringResource(R.string.place_saved_snackbar)
    val shareChooserTitle = stringResource(R.string.place_share)
    val markedVisitedMessage = stringResource(R.string.marked_as_visited)
    val removedVisitedMessage = stringResource(R.string.visited_removed)
    val place = placeState.dataOrNull
    val isSaved = savedIds.contains(place?.id)
    val isVisited = visitedIds.contains(place?.id)

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                PlaceDetailEvent.NavigateBack -> onBackClick()
            }
        }
    }

    DetailBody(
        state = placeState,
        kind = "place",
        onBackClick = onBackClick,
        onRetry = viewModel::retry
    ) { loadedPlace ->
        val region = loadedPlace.displayRegion
        val shareText = stringResource(R.string.place_share_text, loadedPlace.name)
        val onShareClick = {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            context.startActivity(Intent.createChooser(intent, shareChooserTitle))
        }
        val onDirectionsClick = {
            val latitude = loadedPlace.latitude
            val longitude = loadedPlace.longitude
            val uri = if (latitude != null && longitude != null) {
                Uri.parse(
                    "geo:$latitude,$longitude?q=$latitude,$longitude" +
                        "(${Uri.encode(loadedPlace.name)})"
                )
            } else {
                val query = Uri.encode(
                    listOf(loadedPlace.name, region, "Nepal")
                        .filter { it.isNotBlank() }
                        .joinToString(", ")
                )
                Uri.parse("geo:0,0?q=$query")
            }
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
        PlaceDetailContent(
            place = loadedPlace,
            classificationBadges = loadedPlace.badges()
                // A living community site is not presented as a monument.
                .filterNot {
                    loadedPlace.livingHeritage && it == PlaceBadge.HISTORIC_MONUMENT
                }
                .map { stringResource(placeBadgeLabelRes(it)) },
            relatedPlaces = relatedContent.places,
            relatedStories = relatedContent.stories,
            isSaved = isSaved,
            isVisited = isVisited,
            onSaveClick = {
                if (viewModel.toggleSave()) {
                    scope.launch { snackbarHost.showSnackbar(savedMessage) }
                }
            },
            onVisitedClick = {
                val nowVisited = viewModel.toggleVisited()
                scope.launch {
                    snackbarHost.showSnackbar(
                        if (nowVisited) markedVisitedMessage else removedVisitedMessage
                    )
                }
            },
            onBackClick = onBackClick,
            onShareClick = onShareClick,
            onDirectionsClick = onDirectionsClick,
            onPlaceClick = onPlaceClick,
            onStoryClick = onStoryClick,
            snackbarHost = snackbarHost
        )
    }
}

@Composable
private fun PlaceDetailContent(
    place: Place,
    classificationBadges: List<String>,
    relatedPlaces: List<Place>,
    relatedStories: List<Story>,
    isSaved: Boolean,
    isVisited: Boolean,
    onSaveClick: () -> Unit,
    onVisitedClick: () -> Unit,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onDirectionsClick: () -> Unit,
    onPlaceClick: (String) -> Unit,
    onStoryClick: (String) -> Unit,
    snackbarHost: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val region = place.displayRegion
    val rating = place.sourcedRating
    val source = place.sourceLabel.ifBlank { place.sourceUrl }
    val latitude = place.latitude
    val longitude = place.longitude
    val hasCoordinates = latitude != null && longitude != null

    val entryFeeLabel = stringResource(R.string.place_entry_fee)
    val bestTimeLabel = stringResource(R.string.place_best_time)
    val hoursLabel = stringResource(R.string.place_hours)
    val accessibilityLabel = stringResource(R.string.place_accessibility)

    // Facts stay hidden until provenance says they are current. A blank value
    // and an unverified value are both omitted; neither becomes "Not available".
    val practicalRows = buildList {
        if (place.entryFee.isNotBlank()) {
            add(PracticalRow(Icons.Default.ConfirmationNumber, entryFeeLabel, place.entryFee))
        }
        if (place.bestTime.isNotBlank()) {
            add(PracticalRow(Icons.Default.CalendarMonth, bestTimeLabel, place.bestTime))
        }
        if (place.hours.isNotBlank()) {
            add(PracticalRow(Icons.Default.Schedule, hoursLabel, place.hours))
        }
        if (place.accessibility.isNotBlank()) {
            add(PracticalRow(Icons.Default.Place, accessibilityLabel, place.accessibility))
        }
    }
    val showPracticalInfo = place.canShowPracticalInfo && practicalRows.isNotEmpty()
    val heroBadges = buildList {
        if (place.heritageType.isNotBlank()) add(place.heritageType)
        addAll(classificationBadges)
        if (isEmpty() && place.badge.isNotBlank()) add(place.badge)
    }.distinct()

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(hostState = snackbarHost) },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDirectionsClick,
                        modifier = Modifier.size(48.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                StandardCardShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Directions,
                            contentDescription = stringResource(R.string.place_directions),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier.size(48.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                StandardCardShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = stringResource(R.string.place_share),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Button(
                        onClick = onSaveClick,
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSaved) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                            contentColor = if (isSaved) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onPrimary
                            }
                        )
                    ) {
                        Text(
                            text = stringResource(
                                if (isSaved) R.string.place_saved else R.string.place_save
                            ),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                        HeritageImage(
                            imageUrl = place.imageUrl,
                            contentDescription = place.name,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = place.name.ifBlank { stringResource(R.string.untitled) },
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (region.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.Place,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = region,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (heroBadges.isNotEmpty() || rating != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                heroBadges.forEach { label ->
                                    DetailBadge(text = label)
                                }
                                if (rating != null) {
                                    DetailBadge(
                                        text = stringResource(R.string.place_rating_sourced, rating)
                                    )
                                }
                            }
                        }
                        // Explicit visit tracking: the user must choose this;
                        // merely opening the detail screen is not a visit.
                        OutlinedButton(onClick = onVisitedClick, shape = BadgeShape) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = stringResource(
                                    if (isVisited) R.string.visited_badge else R.string.mark_as_visited
                                ),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                if (place.significance.isNotBlank()) {
                    item {
                        DetailSection(title = stringResource(R.string.place_why_it_matters)) {
                            Text(
                                text = place.significance,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                if (place.description.isNotBlank()) {
                    item {
                        DetailSection(
                            title = stringResource(
                                if (place.livingHeritage) {
                                    R.string.place_about_living
                                } else {
                                    R.string.place_about
                                }
                            )
                        ) {
                            Text(
                                text = place.description,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                            )
                        }
                    }
                }

                if (showPracticalInfo) {
                    item {
                        DetailSection(
                            title = stringResource(
                                if (place.livingHeritage) {
                                    R.string.place_visitor_info
                                } else {
                                    R.string.place_practical_info
                                }
                            )
                        ) {
                            Card(
                                shape = StandardCardShape,
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                ) {
                                    practicalRows.forEachIndexed { index, row ->
                                        MetaRow(
                                            icon = row.icon,
                                            label = row.label,
                                            value = row.value,
                                            modifier = Modifier.padding(vertical = 10.dp)
                                        )
                                        if (index != practicalRows.lastIndex) {
                                            HorizontalDivider(
                                                color = MaterialTheme.colorScheme.outline
                                                    .copy(alpha = 0.25f)
                                            )
                                        }
                                    }
                                }
                            }
                            if (source.isNotBlank() || place.lastVerifiedAt.isNotBlank()) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    if (source.isNotBlank()) {
                                        Text(
                                            text = stringResource(R.string.place_info_source, source),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (place.lastVerifiedAt.isNotBlank()) {
                                        Text(
                                            text = stringResource(
                                                R.string.place_info_last_verified,
                                                place.lastVerifiedAt
                                            ),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (place.livingHeritage) {
                    item {
                        DetailSection(
                            title = stringResource(R.string.place_living_heritage_title)
                        ) {
                            Text(
                                text = stringResource(R.string.place_living_heritage_body),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                            )
                            if (place.etiquette.isNotBlank()) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = stringResource(R.string.place_visit_respectfully),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = place.etiquette,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                if (region.isNotBlank() || hasCoordinates) {
                    item {
                        DetailSection(title = stringResource(R.string.place_location)) {
                            if (region.isNotBlank()) {
                                Text(
                                    text = region,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            if (latitude != null && longitude != null) {
                                MapPreviewCard(
                                    latitude = latitude,
                                    longitude = longitude,
                                    onOpenClick = onDirectionsClick
                                )
                            } else {
                                OutlinedButton(
                                    onClick = onDirectionsClick,
                                    shape = StandardCardShape
                                ) {
                                    Icon(
                                        Icons.Default.Map,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(stringResource(R.string.place_open_in_maps))
                                }
                            }
                        }
                    }
                }

                if (relatedStories.isNotEmpty()) {
                    item {
                        DetailSection(
                            title = stringResource(
                                if (relatedStories.size == 1) {
                                    R.string.place_related_story
                                } else {
                                    R.string.place_related_stories
                                }
                            )
                        ) {
                            relatedStories.forEach { story ->
                                RelatedStoryCard(
                                    story = story,
                                    onClick = { onStoryClick(story.id) }
                                )
                            }
                        }
                    }
                }

                if (relatedPlaces.isNotEmpty()) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            DetailSectionTitle(
                                text = stringResource(R.string.place_connected_places),
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(relatedPlaces, key = { it.id }) { related ->
                                    RelatedPlaceCard(
                                        place = related,
                                        onClick = { onPlaceClick(related.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            PhotoOverlayIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.TopStart)
                    .detailBackButtonPadding()
            )
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        DetailSectionTitle(text = title)
        content()
    }
}

@Composable
private fun MapPreviewCard(
    latitude: Double,
    longitude: Double,
    onOpenClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onOpenClick),
        shape = StandardCardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(132.dp)) {
            Icon(
                Icons.Default.Map,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Center)
                    .size(96.dp)
                    .alpha(0.18f)
            )
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    Icons.Default.Place,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(R.string.place_map_preview),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(
                        R.string.place_coordinates,
                        String.format(Locale.US, "%.4f", latitude),
                        String.format(Locale.US, "%.4f", longitude)
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(
                modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Default.Map,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = stringResource(R.string.place_open_in_maps),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun RelatedStoryCard(story: Story, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = StandardCardShape,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(76.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                HeritageImage(
                    imageUrl = story.imageUrl,
                    contentDescription = story.title,
                    fallbackIcon = Icons.Default.AutoStories,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = story.title.ifBlank { stringResource(R.string.untitled) },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                val summary = story.subtitle.ifBlank { story.description }
                if (summary.isNotBlank()) {
                    Text(
                        text = summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = stringResource(R.string.place_read_story),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun RelatedPlaceCard(place: Place, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(196.dp).clickable(onClick = onClick),
        shape = StandardCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth().height(112.dp),
                contentAlignment = Alignment.Center
            ) {
                HeritageImage(
                    imageUrl = place.imageUrl,
                    contentDescription = place.name,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = place.name.ifBlank { stringResource(R.string.untitled) },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                val region = place.displayRegion
                if (region.isNotBlank()) {
                    Text(
                        text = region,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Preview(name = "Place Detail — Light")
@Preview(
    name = "Place Detail — Dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PlaceDetailPreview() {
    AvalokanTheme {
        PlaceDetailContent(
            place = Place(
                id = "kathmandu-durbar",
                name = "Kathmandu Durbar Square",
                description = "Once the royal palace of the Malla kings, the square still holds " +
                    "temples, courtyards and the living routines of the old city.",
                significance = "The political and ceremonial heart of the Kathmandu Valley " +
                    "for centuries, and still a place where Newa ritual life continues.",
                heritageType = "Durbar Square",
                district = "Kathmandu",
                province = "Bagmati",
                entryFee = "Rs. 1,000 (foreign visitors)",
                hours = "9 AM – 5 PM",
                sourceLabel = "Department of Archaeology",
                lastVerifiedAt = "2026-10-01"
            ),
            classificationBadges = listOf("UNESCO Heritage"),
            relatedPlaces = listOf(
                Place(
                    id = "patan-durbar",
                    name = "Patan Durbar Square",
                    description = "",
                    district = "Lalitpur",
                    imageUrl = null
                )
            ),
            relatedStories = listOf(
                Story(
                    id = "boudhanath",
                    title = "The Sacred Echoes of Boudhanath",
                    subtitle = "A living mandala"
                )
            ),
            isSaved = false,
            isVisited = false,
            onSaveClick = {},
            onVisitedClick = {},
            onBackClick = {},
            onShareClick = {},
            onDirectionsClick = {},
            onPlaceClick = {},
            onStoryClick = {},
            snackbarHost = SnackbarHostState()
        )
    }
}
