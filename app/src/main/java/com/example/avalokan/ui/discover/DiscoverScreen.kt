package com.example.avalokan.ui.discover

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExploreOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.avalokan.R
import com.example.avalokan.domain.model.Place
import com.example.avalokan.ui.common.ContentState
import com.example.avalokan.ui.components.DiscoverLoadingState
import com.example.avalokan.ui.components.StateMessage
import com.example.avalokan.ui.common.heritageFilterHelpRes
import com.example.avalokan.ui.common.heritageFilterLabel
import com.example.avalokan.ui.common.placeBadgeLabel
import com.example.avalokan.ui.theme.AvalokanTheme
import com.example.avalokan.ui.theme.BadgeShape
import com.example.avalokan.ui.theme.Spacing
import com.example.avalokan.ui.theme.discoverCategoryAccent
import com.example.avalokan.ui.theme.discoverCategoryOnAccent

@Composable
fun DiscoverScreen(
    onPlaceClick: (String) -> Unit = {},
    onExploreEventsClick: () -> Unit = {},
    viewModel: DiscoverViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsState()
    val selectedChip by viewModel.selectedCategory.collectAsState()
    val sites by viewModel.sites.collectAsState()
    val contentState by viewModel.contentState.collectAsState()
    val savedIds by viewModel.savedIds.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val availableCategories by viewModel.availableCategories.collectAsState()
    val recommended by viewModel.recommended.collectAsState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = viewModel::refresh,
        modifier = Modifier.fillMaxSize()
    ) {
        DiscoverContent(
            query = query,
            onQuery = viewModel::onQueryChange,
            filters = DiscoverCategories,
            selected = selectedChip,
            onSelect = viewModel::onCategorySelect,
            sites = sites,
            contentState = contentState,
            savedIds = savedIds,
            onToggleSave = { viewModel.toggleSave(it) },
            onPlaceClick = onPlaceClick,
            onRetry = viewModel::retry,
            availableCategories = availableCategories,
            recommended = recommended,
            onClearQuery = viewModel::clearQuery,
            onSuggestionSelect = viewModel::onSuggestionSelect,
            onExploreEventsClick = onExploreEventsClick
        )
    }
}

@Composable
private fun DiscoverContent(
    query: String,
    onQuery: (String) -> Unit,
    filters: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
    sites: List<Place>,
    contentState: ContentState<List<Place>>,
    savedIds: Set<String>,
    onToggleSave: (String) -> Unit,
    onPlaceClick: (String) -> Unit,
    onRetry: () -> Unit,
    availableCategories: List<String>,
    recommended: List<Place>,
    onClearQuery: () -> Unit,
    onSuggestionSelect: (String) -> Unit,
    onExploreEventsClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = Spacing.large),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(Spacing.small))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.discover),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        SearchField(
            query = query, onQuery = onQuery,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Suggestions(
            filters = filters, selected = selected,
            onSelect = onSelect,
            query = query,
            sites = sites,
            contentState = contentState,
            savedIds = savedIds,
            onToggleSave = onToggleSave,
            onPlaceClick = onPlaceClick,
            onRetry = onRetry,
            availableCategories = availableCategories,
            recommended = recommended,
            onClearQuery = onClearQuery,
            onSuggestionSelect = onSuggestionSelect,
            onExploreEventsClick = onExploreEventsClick
        )
    }
}

@Composable
private fun SearchField(
    query: String,
    onQuery: (String) -> Unit,
    modifier: Modifier = Modifier
){
    OutlinedTextField(
        value = query,
        onValueChange = onQuery,
        modifier = modifier.fillMaxWidth()
            .height(56.dp),
        placeholder = {
            Text(
                text = stringResource(R.string.search),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription =  null,
                modifier = Modifier.size(18.dp)
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQuery("") },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.clear_search),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = CircleShape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
private fun Suggestions(
    filters: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
    query: String,
    sites: List<Place>,
    contentState: ContentState<List<Place>>,
    savedIds: Set<String>,
    onToggleSave: (String) -> Unit,
    onPlaceClick: (String) -> Unit,
    onRetry: () -> Unit,
    availableCategories: List<String>,
    recommended: List<Place>,
    onClearQuery: () -> Unit,
    onSuggestionSelect: (String) -> Unit,
    onExploreEventsClick: () -> Unit,
    modifier: Modifier = Modifier
){
    val activeFilter = filters.getOrNull(selected) ?: filters.firstOrNull() ?: "All"
    val activeFilterLabel = heritageFilterLabel(activeFilter)
    val suggestionCategories = availableCategories
        .filter { it != "All" && it != activeFilter }
        .take(4)
    val suggestionLabels = suggestionCategories.associateWith { heritageFilterLabel(it) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ){
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            itemsIndexed(filters) { index, label ->
                val isSelected = index == selected
                val chipAccent = discoverCategoryAccent(label)
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelect(index) },
                    label = { Text(text = heritageFilterLabel(label), style = MaterialTheme.typography.bodyMedium, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                    shape = BadgeShape,
                    modifier = Modifier.then(
                        if (isSelected) Modifier.shadow(4.dp, BadgeShape)
                        else Modifier
                    ),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = chipAccent,
                        selectedLabelColor = discoverCategoryOnAccent(label),
                        containerColor = Color.Transparent,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        selectedBorderColor = Color.Transparent,
                        borderWidth = 1.dp
                    )
                )
            }
        }
        val helpRes = heritageFilterHelpRes(activeFilter)
        if (helpRes != null) {
            Text(
                text = stringResource(helpRes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
        when (contentState) {
            ContentState.Loading -> DiscoverLoadingState()

            is ContentState.Error -> StateMessage(
                icon = Icons.Default.ExploreOff,
                title = stringResource(R.string.load_places_error_title),
                message = stringResource(R.string.load_places_error_message),
                actionLabel = stringResource(R.string.retry),
                onAction = onRetry,
                secondaryActionLabel = stringResource(R.string.explore_events),
                onSecondaryAction = onExploreEventsClick
            )

            // The repository answered, and it has no places. That is an empty
            // catalogue, not a connection failure.
            ContentState.Empty -> StateMessage(
                icon = Icons.Default.ExploreOff,
                title = stringResource(R.string.empty_places_title),
                message = stringResource(R.string.empty_places_message),
                actionLabel = stringResource(R.string.explore_events),
                onAction = onExploreEventsClick
            )

            is ContentState.Content -> when {
                sites.isNotEmpty() -> Column(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    sites.forEach { site ->
                        SearchPlaceCard(
                            place = site,
                            isSaved = savedIds.contains(site.id),
                            onSaveClick = { onToggleSave(site.id) },
                            onCardClick = { onPlaceClick(site.id) }
                        )
                    }
                }

                // A search term is what excluded everything — offer a way forward.
                query.isNotBlank() -> {
                    StateMessage(
                        icon = Icons.Default.SearchOff,
                        title = stringResource(R.string.no_results_title, query.trim()),
                        message = stringResource(R.string.no_results_message),
                        actionLabel = stringResource(R.string.clear_search),
                        onAction = onClearQuery,
                        secondaryActionLabel = stringResource(R.string.browse_all_places),
                        onSecondaryAction = { onSuggestionSelect("All") },
                        suggestions = suggestionCategories,
                        suggestionsLabel = stringResource(R.string.try_these),
                        suggestionLabels = suggestionLabels,
                        onSuggestionClick = onSuggestionSelect
                    )
                    if (recommended.isNotEmpty()) {
                        PopularNow(
                            places = recommended,
                            savedIds = savedIds,
                            onToggleSave = onToggleSave,
                            onPlaceClick = onPlaceClick
                        )
                    }
                }

                // Places loaded fine; this filter simply has none.
                else -> StateMessage(
                    icon = Icons.Default.ExploreOff,
                    title = stringResource(R.string.nothing_in_category_title, activeFilterLabel),
                    message = stringResource(R.string.nothing_in_category_message, activeFilterLabel),
                    actionLabel = stringResource(R.string.show_all_places),
                    onAction = { onSelect(0) },
                    suggestions = suggestionCategories,
                    suggestionsLabel = stringResource(R.string.try_these),
                    suggestionLabels = suggestionLabels,
                    onSuggestionClick = onSuggestionSelect
                )
            }
        }
    }
}

/** Real content shown under a failed search, so the screen still leads somewhere. */
@Composable
private fun PopularNow(
    places: List<Place>,
    savedIds: Set<String>,
    onToggleSave: (String) -> Unit,
    onPlaceClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.popular_now),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )
        places.forEach { place ->
            SearchPlaceCard(
                place = place,
                isSaved = savedIds.contains(place.id),
                onSaveClick = { onToggleSave(place.id) },
                onCardClick = { onPlaceClick(place.id) }
            )
        }
    }
}

@Composable
private fun SearchPlaceCard(
    place: Place,
    isSaved: Boolean = false,
    onSaveClick: () -> Unit = {},
    onCardClick: () -> Unit = {}
) {
    val classificationBadge = placeBadgeLabel(place)
    com.example.avalokan.ui.components.HeritageCard(
        title = place.name,
        subtitle = place.meta.ifBlank { stringResource(R.string.no_location) }.uppercase(),
        subtitleStyle = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
        badge = classificationBadge,
        rating = place.sourcedRating,
        ratingSource = place.sourceLabel.ifBlank { place.sourceUrl },
        description = place.description.ifBlank { stringResource(R.string.no_description) },
        imageHeight = 180.dp,
        imageCorner = 28.dp,
        imageUrl = place.imageUrl,
        showSave = true,
        isSaved = isSaved,
        onSaveClick = onSaveClick,
        onCardClick = onCardClick,
        cardContainer = true
    )
}

@Preview(name = "Discover — Light")
@Preview(
    name = "Discover — Dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun DiscoverPreview(){
    AvalokanTheme(){
        val previewPlaces = listOf(
            Place(
                id = "kathmandu-durbar",
                name = "Kathmandu Durbar Square",
                description = "The heart of old Kathmandu city.",
                meta = "Kathmandu • Historical Site",
                category = "Historical",
                rating = "4.8",
                typologies = listOf("DURBAR", "PAGODA")
            ),
            Place(
                id = "lumbini-garden",
                name = "Lumbini Garden",
                description = "The sacred birthplace of Lord Buddha.",
                meta = "Lumbini • Spiritual Site",
                category = "Cultural",
                rating = "4.9",
                typologies = listOf("BUDDHIST_SACRED_SITE", "PILGRIMAGE_LANDSCAPE")
            )
        )
        DiscoverContent(
            query = "",
            onQuery = {},
            filters = DiscoverCategories,
            selected = 0,
            onSelect = {},
            sites = previewPlaces,
            contentState = ContentState.Content(previewPlaces),
            savedIds = emptySet(),
            onToggleSave = {},
            onPlaceClick = {},
            onRetry = {},
            availableCategories = listOf("All", "PAGODA_NEWA", "STUPA_MONASTERY"),
            recommended = emptyList(),
            onClearQuery = {},
            onSuggestionSelect = {},
            onExploreEventsClick = {}
        )
    }
}
