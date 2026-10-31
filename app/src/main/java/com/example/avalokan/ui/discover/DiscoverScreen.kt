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
import androidx.compose.material.icons.filled.Tune
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
import com.example.avalokan.data.place.PlaceItem
import com.example.avalokan.ui.components.DiscoverLoadingState
import com.example.avalokan.ui.components.StateMessage
import com.example.avalokan.ui.theme.AvalokanTheme
import com.example.avalokan.ui.theme.BadgeShape
import com.example.avalokan.ui.theme.GreyLight
import com.example.avalokan.ui.theme.Spacing
import com.example.avalokan.ui.theme.TextPrimary
import com.example.avalokan.ui.theme.TextSecondary
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
    val totalPlaces by viewModel.totalPlaces.collectAsState()
    val savedIds by viewModel.savedIds.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
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
            totalPlaces = totalPlaces,
            savedIds = savedIds,
            onToggleSave = { viewModel.toggleSave(it) },
            onPlaceClick = onPlaceClick,
            onRetry = viewModel::refresh,
            isLoading = isLoading,
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
    sites: List<PlaceItem>,
    totalPlaces: Int,
    savedIds: Set<String>,
    onToggleSave: (String) -> Unit,
    onPlaceClick: (String) -> Unit,
    onRetry: () -> Unit,
    isLoading: Boolean,
    availableCategories: List<String>,
    recommended: List<PlaceItem>,
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.discover),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(
                onClick = { /* TODO: filters */ },
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
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
            totalPlaces = totalPlaces,
            savedIds = savedIds,
            onToggleSave = onToggleSave,
            onPlaceClick = onPlaceClick,
            onRetry = onRetry,
            isLoading = isLoading,
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
            focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = GreyLight,
            unfocusedContainerColor = GreyLight,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedPlaceholderColor = TextSecondary,
            unfocusedPlaceholderColor = TextSecondary,
            focusedLeadingIconColor = TextSecondary,
            unfocusedLeadingIconColor = TextSecondary
        )
    )
}

@Composable
private fun Suggestions(
    filters: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
    query: String,
    sites: List<PlaceItem>,
    totalPlaces: Int,
    savedIds: Set<String>,
    onToggleSave: (String) -> Unit,
    onPlaceClick: (String) -> Unit,
    onRetry: () -> Unit,
    isLoading: Boolean,
    availableCategories: List<String>,
    recommended: List<PlaceItem>,
    onClearQuery: () -> Unit,
    onSuggestionSelect: (String) -> Unit,
    onExploreEventsClick: () -> Unit,
    modifier: Modifier = Modifier
){
    val activeFilter = filters.getOrNull(selected) ?: filters.firstOrNull() ?: "All"
    val suggestionCategories = availableCategories
        .filter { it != "All" && it != activeFilter }
        .take(4)

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
                    label = { Text(text = label, style = MaterialTheme.typography.bodyMedium, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
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
        when {
            // First paint: the cache is empty but the network may still answer.
            isLoading && sites.isEmpty() -> DiscoverLoadingState()

            sites.isNotEmpty() -> Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                sites.forEach { site ->
                    SearchPlaceCard(
                        title = site.name,
                        meta = site.meta,
                        rating = site.rating,
                        desc = site.description,
                        imageUrl = site.imageUrl,
                        isSaved = savedIds.contains(site.id),
                        onSaveClick = { onToggleSave(site.id) },
                        onCardClick = { onPlaceClick(site.id) }
                    )
                }
            }

            // Nothing arrived from the repository at all — this really is a load failure.
            totalPlaces == 0 -> StateMessage(
                icon = Icons.Default.ExploreOff,
                title = stringResource(R.string.load_places_error_title),
                message = stringResource(R.string.load_places_error_message),
                actionLabel = stringResource(R.string.retry),
                onAction = onRetry,
                secondaryActionLabel = stringResource(R.string.explore_events),
                onSecondaryAction = onExploreEventsClick
            )

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
                title = stringResource(R.string.nothing_in_category_title, activeFilter),
                message = stringResource(R.string.nothing_in_category_message, activeFilter),
                actionLabel = stringResource(R.string.show_all_places),
                onAction = { onSelect(0) },
                suggestions = suggestionCategories,
                suggestionsLabel = stringResource(R.string.try_these),
                onSuggestionClick = onSuggestionSelect
            )
        }
    }
}

/** Real content shown under a failed search, so the screen still leads somewhere. */
@Composable
private fun PopularNow(
    places: List<PlaceItem>,
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
                title = place.name,
                meta = place.meta,
                rating = place.rating,
                desc = place.description,
                imageUrl = place.imageUrl,
                isSaved = savedIds.contains(place.id),
                onSaveClick = { onToggleSave(place.id) },
                onCardClick = { onPlaceClick(place.id) }
            )
        }
    }
}

@Composable
private fun SearchPlaceCard(
    title: String,
    meta: String,
    rating: String,
    desc: String,
    imageUrl: String? = null,
    isSaved: Boolean = false,
    onSaveClick: () -> Unit = {},
    onCardClick: () -> Unit = {}
){
    com.example.avalokan.ui.components.HeritageCard(
        title = title,
        subtitle = meta.uppercase(),
        subtitleStyle = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
        rating = rating,
        description = desc,
        imageHeight = 180.dp,
        imageCorner = 28.dp,
        imageUrl = imageUrl,
        showSave = true,
        isSaved = isSaved,
        onSaveClick = onSaveClick,
        onCardClick = onCardClick,
        cardContainer = true
    )
}

@Preview
@Composable
private fun DiscoverPreview(){
    AvalokanTheme(){
        DiscoverContent(
            query = "",
            onQuery = {},
            filters = DiscoverCategories,
            selected = 0,
            onSelect = {},
            sites = listOf(
                PlaceItem(
                    id = "kathmandu-durbar",
                    name = "Kathmandu Durbar Square",
                    description = "The heart of old Kathmandu city.",
                    meta = "Kathmandu • Historical Site",
                    category = "Historical",
                    rating = "4.8"
                ),
                PlaceItem(
                    id = "lumbini-garden",
                    name = "Lumbini Garden",
                    description = "The sacred birthplace of Lord Buddha.",
                    meta = "Lumbini • Spiritual Site",
                    category = "Cultural",
                    rating = "4.9"
                )
            ),
            totalPlaces = 2,
            savedIds = emptySet(),
            onToggleSave = {},
            onPlaceClick = {},
            onRetry = {},
            isLoading = false,
            availableCategories = listOf("All", "Historical", "Cultural"),
            recommended = emptyList(),
            onClearQuery = {},
            onSuggestionSelect = {},
            onExploreEventsClick = {}
        )
    }
}
