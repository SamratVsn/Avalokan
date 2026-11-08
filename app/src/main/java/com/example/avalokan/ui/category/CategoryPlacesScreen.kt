package com.example.avalokan.ui.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExploreOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.avalokan.R
import com.example.avalokan.domain.model.Place
import com.example.avalokan.ui.common.ContentState
import com.example.avalokan.ui.common.placeBadgeLabel
import com.example.avalokan.ui.components.DiscoverLoadingState
import com.example.avalokan.ui.components.HeritageCard
import com.example.avalokan.ui.components.StateMessage
import com.example.avalokan.ui.theme.AvalokanTheme
import com.example.avalokan.ui.theme.Spacing

@Composable
fun CategoryPlacesScreen(
    onBackClick: () -> Unit = {},
    onPlaceClick: (String) -> Unit = {},
    onBrowseAllClick: () -> Unit = {},
    viewModel: CategoryPlacesViewModel = hiltViewModel()
) {
    val places by viewModel.places.collectAsState()
    val contentState by viewModel.contentState.collectAsState()
    val savedIds by viewModel.savedIds.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    CategoryPlacesContent(
        title = categoryTitle(viewModel.category),
        category = viewModel.category,
        places = places,
        contentState = contentState,
        savedIds = savedIds,
        isRefreshing = isRefreshing,
        onBackClick = onBackClick,
        onPlaceClick = onPlaceClick,
        onToggleSave = viewModel::toggleSave,
        onRefresh = viewModel::refresh,
        onRetry = viewModel::retry,
        onBrowseAllClick = onBrowseAllClick
    )
}

@Composable
private fun categoryTitle(category: String): String {
    val titleRes = when (category.lowercase()) {
        "historical" -> R.string.category_historical
        "cultural" -> R.string.category_cultural
        "spiritual" -> R.string.category_spiritual
        "nature" -> R.string.category_nature
        "adventure" -> R.string.category_adventure
        else -> null
    }
    return if (titleRes != null) {
        stringResource(titleRes)
    } else {
        category.ifBlank { stringResource(R.string.explore_places) }
    }
}

@Composable
private fun CategoryPlacesContent(
    title: String,
    category: String,
    places: List<Place>,
    contentState: ContentState<List<Place>>,
    savedIds: Set<String>,
    isRefreshing: Boolean,
    onBackClick: () -> Unit,
    onPlaceClick: (String) -> Unit,
    onToggleSave: (String) -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onBrowseAllClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        Spacer(Modifier.height(Spacing.extraSmall))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.sidePadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(36.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (places.isNotEmpty()) {
                    Text(
                        text = pluralStringResource(
                            R.plurals.places_count,
                            places.size,
                            places.size
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            when (contentState) {
                ContentState.Loading -> DiscoverLoadingState()

                is ContentState.Error -> StateMessage(
                    icon = Icons.Default.ExploreOff,
                    title = stringResource(R.string.load_places_error_title),
                    message = stringResource(R.string.load_places_error_message),
                    actionLabel = stringResource(R.string.retry),
                    onAction = onRetry,
                    secondaryActionLabel = stringResource(R.string.browse_all_places),
                    onSecondaryAction = onBrowseAllClick
                )

                // The repository answered, and it has no places at all.
                ContentState.Empty -> StateMessage(
                    icon = Icons.Default.ExploreOff,
                    title = stringResource(R.string.empty_places_title),
                    message = stringResource(R.string.empty_places_message),
                    actionLabel = stringResource(R.string.browse_all_places),
                    onAction = onBrowseAllClick
                )

                is ContentState.Content -> if (places.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 24.dp,
                            end = 24.dp,
                            top = 4.dp,
                            bottom = Spacing.large
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(places, key = { it.id }) { place ->
                            CategoryPlaceCard(
                                place = place,
                                isSaved = savedIds.contains(place.id),
                                onToggleSave = { onToggleSave(place.id) },
                                onPlaceClick = { onPlaceClick(place.id) }
                            )
                        }
                    }
                } else {
                    // Places loaded, but none carry this category.
                    StateMessage(
                        icon = Icons.Default.ExploreOff,
                        title = stringResource(
                            R.string.nothing_in_category_title,
                            category.ifBlank { title }
                        ),
                        message = stringResource(
                            R.string.nothing_in_category_message,
                            category.ifBlank { title }
                        ),
                        actionLabel = stringResource(R.string.browse_all_places),
                        onAction = onBrowseAllClick
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryPlaceCard(
    place: Place,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    onPlaceClick: () -> Unit
) {
    HeritageCard(
        title = place.name,
        subtitle = place.meta.ifBlank { stringResource(R.string.no_location) }.uppercase(),
        subtitleStyle = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
        badge = placeBadgeLabel(place),
        rating = place.sourcedRating,
        ratingSource = place.sourceLabel.ifBlank { place.sourceUrl },
        description = place.description.ifBlank { stringResource(R.string.no_description) },
        imageHeight = 180.dp,
        imageCorner = 28.dp,
        imageUrl = place.imageUrl,
        showSave = true,
        isSaved = isSaved,
        onSaveClick = onToggleSave,
        onCardClick = onPlaceClick,
        cardContainer = true
    )
}

@Preview
@Composable
private fun CategoryPlacesPreview() {
    AvalokanTheme {
        CategoryPlacesContent(
            title = "Historical Gems",
            category = "Historical",
            places = emptyList(),
            contentState = ContentState.Empty,
            savedIds = emptySet(),
            isRefreshing = false,
            onBackClick = {},
            onPlaceClick = {},
            onToggleSave = {},
            onRefresh = {},
            onRetry = {},
            onBrowseAllClick = {}
        )
    }
}
