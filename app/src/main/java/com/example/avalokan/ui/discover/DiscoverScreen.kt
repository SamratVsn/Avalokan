package com.example.avalokan.ui.discover

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ExploreOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.avalokan.R
import com.example.avalokan.data.place.PlaceItem
import com.example.avalokan.ui.components.StateMessage
import com.example.avalokan.ui.theme.AccentMarigold
import com.example.avalokan.ui.theme.AvalokanTheme
import com.example.avalokan.ui.theme.BadgeShape
import com.example.avalokan.ui.theme.GreyLight
import com.example.avalokan.ui.theme.PrimaryLight
import com.example.avalokan.ui.theme.PrimaryTeal
import com.example.avalokan.ui.theme.Spacing
import com.example.avalokan.ui.theme.StandardCardShape
import com.example.avalokan.ui.theme.TextPrimary
import com.example.avalokan.ui.theme.TextSecondary

@Composable
fun DiscoverScreen(
    onPlaceClick: (String) -> Unit = {},
    viewModel: DiscoverViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsState()
    val selectedChip by viewModel.selectedCategory.collectAsState()
    val sites by viewModel.sites.collectAsState()
    val totalPlaces by viewModel.totalPlaces.collectAsState()
    val savedIds by viewModel.savedIds.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

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
            onRetry = viewModel::refresh
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
    onRetry: () -> Unit
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
            onRetry = onRetry
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
            .height(48.dp),
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
    modifier: Modifier = Modifier
){
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
                        selectedContainerColor = PrimaryTeal, selectedLabelColor = Color.White,
                        containerColor = Color.Transparent, labelColor = MaterialTheme.colorScheme.onSurfaceVariant
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
        if (sites.isEmpty()) {
            val activeFilter = filters.getOrNull(selected) ?: filters.firstOrNull() ?: "All"
            when {
                // A search term is what excluded everything — talk about the query.
                query.isNotBlank() -> StateMessage(
                    icon = Icons.Default.SearchOff,
                    title = "No matches found",
                    message = "We couldn’t find anything for “$query”. Try a different search or category."
                )
                // Nothing arrived from the repository at all — this really is a load failure.
                totalPlaces == 0 -> StateMessage(
                    icon = Icons.Default.ExploreOff,
                    title = "We couldn’t load places",
                    message = "Your connection may be off. Reconnect and refresh to explore heritage sites across Nepal.",
                    actionLabel = "Retry",
                    onAction = onRetry
                )
                // Places loaded fine; this filter simply has none.
                else -> StateMessage(
                    icon = Icons.Default.ExploreOff,
                    title = "Nothing in $activeFilter yet",
                    message = "No places are tagged $activeFilter right now — pick another category to keep exploring.",
                    actionLabel = "Show all",
                    onAction = { onSelect(0) }
                )
            }
        } else {
            Column(
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
            onRetry = {}
        )
    }
}