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

package com.example.avalokan.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ExploreOff
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.avalokan.R
import com.example.avalokan.domain.model.Place
import com.example.avalokan.domain.model.Story
import com.example.avalokan.ui.common.ContentState
import com.example.avalokan.ui.common.dataOrNull
import com.example.avalokan.ui.common.placeBadgeLabel
import com.example.avalokan.ui.common.placeCategoryTitle
import com.example.avalokan.ui.components.HomeLoadingState
import com.example.avalokan.ui.components.SkeletonBlock
import com.example.avalokan.ui.components.StateMessage
import com.example.avalokan.ui.theme.AvalokanTheme
import com.example.avalokan.ui.theme.BadgeShape
import com.example.avalokan.ui.theme.EditorialLargeCardShape
import com.example.avalokan.ui.theme.ImageScrim
import com.example.avalokan.ui.theme.ImageScrimStrong
import com.example.avalokan.ui.theme.OnImage
import com.example.avalokan.ui.theme.Spacing
import com.example.avalokan.ui.theme.StandardButtonShape
import com.example.avalokan.ui.theme.StandardCardShape

@Composable
fun AvalokanHome(
    modifier: Modifier = Modifier,
    onStoryClick: (String) -> Unit = {},
    onPlaceClick: (String) -> Unit = {},
    onExploreEventsClick: () -> Unit = {},
    onViewAllCategoryClick: (String) -> Unit = {},
    onViewAllPlacesClick: () -> Unit = {},
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val placesState by viewModel.placesState.collectAsState()
    val heroStoryState by viewModel.heroStoryState.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = viewModel::refresh,
        modifier = modifier.fillMaxSize()
    ) {
        HomeContent(
            heroStoryState = heroStoryState,
            placesState = placesState,
            categories = categories,
            onStoryClick = onStoryClick,
            onPlaceClick = onPlaceClick,
            onExploreEventsClick = onExploreEventsClick,
            onViewAllCategoryClick = onViewAllCategoryClick,
            onViewAllPlacesClick = onViewAllPlacesClick,
            onRetry = viewModel::retry
        )
    }
}

@Composable
private fun HomeContent(
    heroStoryState: ContentState<Story>,
    placesState: ContentState<List<Place>>,
    categories: List<HomeCategory>,
    onStoryClick: (String) -> Unit,
    onPlaceClick: (String) -> Unit,
    onExploreEventsClick: () -> Unit,
    onViewAllCategoryClick: (String) -> Unit,
    onViewAllPlacesClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val places = placesState.dataOrNull.orEmpty()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = Spacing.large),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(Spacing.medium))
        TopIcons()
        StoryCard(
            modifier = Modifier.padding(horizontal = 24.dp),
            storyState = heroStoryState,
            onReadClick = { story -> onStoryClick(story.id) },
            onRetry = onRetry
        )
        when (placesState) {
            ContentState.Loading -> HomeLoadingState()

            is ContentState.Error -> StateMessage(
                icon = Icons.Default.ExploreOff,
                title = stringResource(R.string.load_places_error_title),
                message = stringResource(R.string.load_places_error_message),
                actionLabel = stringResource(R.string.retry),
                onAction = onRetry,
                secondaryActionLabel = stringResource(R.string.explore_events),
                onSecondaryAction = onExploreEventsClick
            )

            ContentState.Empty -> StateMessage(
                icon = Icons.Default.ExploreOff,
                title = stringResource(R.string.empty_places_title),
                message = stringResource(R.string.empty_places_message),
                actionLabel = stringResource(R.string.explore_events),
                onAction = onExploreEventsClick
            )

            is ContentState.Content -> {
                ExploreCategories(
                    categories = categories,
                    onCategoryClick = onViewAllCategoryClick,
                    onBrowseAllClick = onViewAllPlacesClick
                )
                HistoricalGems(
                    places = placesState.data,
                    onPlaceClick = onPlaceClick,
                    onViewAllClick = { onViewAllCategoryClick("Historical") }
                )
                if (places.any { it.featured }) {
                    EditorialPicks(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        places = places,
                        onPlaceClick = onPlaceClick,
                        onViewAllClick = onViewAllPlacesClick
                    )
                }
            }
        }
    }
}

@Composable
private fun TopIcons(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = stringResource(R.string.namaste).uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.explore),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(
            onClick = { /* TODO: notifications */ },
            modifier = Modifier
                .size(36.dp)
                .shadow(4.dp, CircleShape)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = stringResource(R.string.notifications),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun StoryCard(
    storyState: ContentState<Story>,
    modifier: Modifier = Modifier,
    onReadClick: (Story) -> Unit = {},
    onRetry: () -> Unit = {}
) {
    val story = storyState.dataOrNull
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = EditorialLargeCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
            // Hero image from the story stream; gradient placeholder until it loads.
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (story?.imageUrl != null) {
                    coil.compose.AsyncImage(
                        model = story.imageUrl,
                        contentDescription = story.title,
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            // Dark scrim so badge/title/button stay legible over any future photo
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Transparent,
                            0.45f to ImageScrim,
                            1f to ImageScrimStrong
                        )
                    )
            )
            if (storyState is ContentState.Content) {
                Surface(
                    modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                    shape = BadgeShape,
                    color = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ) {
                    Text(
                        text = stringResource(R.string.storyOfTheDay).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Column(
                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                when (storyState) {
                    ContentState.Loading -> {
                        // A story is still on its way. Show shaped placeholders
                        // instead of the empty copy, which would be a lie here.
                        SkeletonBlock(
                            Modifier.fillMaxWidth(0.72f).height(24.dp)
                        )
                        SkeletonBlock(
                            Modifier.fillMaxWidth(0.5f).height(12.dp)
                        )
                    }

                    ContentState.Empty -> {
                        Text(
                            text = stringResource(R.string.no_story_title),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 24.sp,
                                lineHeight = 30.sp
                            ),
                            color = OnImage,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = stringResource(R.string.no_story_summary),
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnImage.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    is ContentState.Error -> {
                        Text(
                            text = stringResource(R.string.load_story_error_title),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 24.sp,
                                lineHeight = 30.sp
                            ),
                            color = OnImage,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = stringResource(R.string.load_story_error_message),
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnImage.copy(alpha = 0.85f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        TextButton(
                            onClick = onRetry,
                            contentPadding = PaddingValues(horizontal = 0.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.retry),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = OnImage
                            )
                        }
                    }

                    is ContentState.Content -> {
                        val loadedStory = storyState.data
                        Text(
                            text = loadedStory.title.takeIf { it.isNotBlank() }
                                ?: stringResource(R.string.untitled),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 24.sp,
                                lineHeight = 30.sp
                            ),
                            color = OnImage,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = loadedStory.subtitle.takeIf { it.isNotBlank() }
                                ?: stringResource(R.string.no_description),
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnImage.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(2.dp))
                        Button(
                            onClick = { onReadClick(loadedStory) },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.readStory),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.size(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = actionLabel,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(onClick = onAction)
        )
    }
}

@Composable
private fun HistoricalGems(
    places: List<Place>,
    onPlaceClick: (String) -> Unit = {},
    onViewAllClick: () -> Unit = {}
) {
    val historical = places.filter { it.category.equals("Historical", ignoreCase = true) }
    if (historical.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
        SectionHeader(
            title = stringResource(R.string.historicalGems),
            actionLabel = stringResource(R.string.viewAll),
            onAction = onViewAllClick,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                count = historical.size,
                key = { index -> historical[index].id }
            ) { index ->
                val place = historical[index]
                PlaceCard(
                    title = place.name,
                    subtitle = place.description.ifBlank { stringResource(R.string.no_description) },
                    badge = placeBadgeLabel(place).orEmpty(),
                    imageUrl = place.imageUrl,
                    onCardClick = { onPlaceClick(place.id) }
                )
            }
        }
    }
}

@Composable
private fun PlaceCard(
    title: String,
    subtitle: String,
    badge: String,
    imageUrl: String? = null,
    onCardClick: () -> Unit = {}
) {
    com.example.avalokan.ui.components.HeritageCard(
        title = title,
        subtitle = subtitle,
        badge = badge,
        imageUrl = imageUrl,
        cardWidth = 150.dp,
        imageHeight = 110.dp,
        imageCorner = 24.dp,
        onCardClick = onCardClick
    )
}

/**
 * Every category that has content, as a two-column grid. Deliberately not a
 * carousel: this is the main breadth entry point, so all categories are visible
 * without horizontal scrolling.
 */
@Composable
private fun ExploreCategories(
    categories: List<HomeCategory>,
    onCategoryClick: (String) -> Unit,
    onBrowseAllClick: () -> Unit
) {
    if (categories.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
        SectionHeader(
            title = stringResource(R.string.explore_by_category),
            actionLabel = stringResource(R.string.viewAll),
            onAction = onBrowseAllClick,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            categories.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { category ->
                        CategoryTile(
                            category = category,
                            onClick = { onCategoryClick(category.name) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CategoryTile(
    category: HomeCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = StandardCardShape,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = placeCategoryTitle(category.name),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = pluralStringResource(
                        R.plurals.places_count,
                        category.count,
                        category.count
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

/**
 * Editorial picks, driven by the curator's `featured` flag. No ratings are
 * shown: "featured" is an editorial decision, not a popularity score.
 */
@Composable
private fun EditorialPicks(
    modifier: Modifier = Modifier,
    places: List<Place> = emptyList(),
    onPlaceClick: (String) -> Unit = {},
    onViewAllClick: () -> Unit = {}
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.small)
    ) {
        SectionHeader(
            title = stringResource(R.string.editorial_picks),
            actionLabel = stringResource(R.string.viewAll),
            onAction = onViewAllClick
        )
        places.filter { it.featured }
            .forEach { place ->
                com.example.avalokan.ui.components.HeritageCard(
                    title = place.name,
                    subtitle = place.meta.ifBlank { stringResource(R.string.no_location) },
                    badge = placeBadgeLabel(place),
                    imageUrl = place.imageUrl,
                    onCardClick = { onPlaceClick(place.id) }
                )
            }
    }
}

@Preview(name = "Home — Light")
@Preview(
    name = "Home — Dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun HomeScreenPreview(){
    AvalokanTheme() {
        HomeContent(
            heroStoryState = ContentState.Content(
                Story(
                    id = "boudhanath",
                    title = "The Sacred Echoes of Boudhanath",
                    subtitle = "A living mandala in the Kathmandu Valley"
                )
            ),
            placesState = ContentState.Content(
                listOf(
                    Place("patan-durbar", "Patan Durbar Square", "Artistic heritage of Lalitpur.", "Lalitpur • Historical Site", "Historical", "4.8"),
                    Place("swayambhu", "Swayambhu", "The Ancient Hill.", "Kathmandu • Spiritual Site", "Historical", "4.7"),
                    Place("boudha-stupa", "Boudha Stupa", "Boudhanath • Spiritual Site.", "Boudhanath • Spiritual Site", "Cultural", "4.9"),
                    Place("bhaktapur-pottery", "Bhaktapur Pottery Square", "Bhaktapur • Craft Quarter.", "Bhaktapur • Craft Quarter", "Cultural", "4.7")
                )
            ),
            categories = listOf(
                HomeCategory("Historical", 2),
                HomeCategory("Cultural", 2)
            ),
            onStoryClick = {},
            onPlaceClick = {},
            onExploreEventsClick = {},
            onViewAllCategoryClick = {},
            onViewAllPlacesClick = {},
            onRetry = {}
        )
    }
}