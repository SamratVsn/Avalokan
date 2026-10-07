package com.example.avalokan.ui.home

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Landscape
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.avalokan.R
import com.example.avalokan.data.place.PlaceItem
import com.example.avalokan.ui.theme.AccentMarigold
import com.example.avalokan.ui.theme.AvalokanTheme
import com.example.avalokan.ui.theme.BadgeShape
import com.example.avalokan.ui.theme.EditorialCardShape
import com.example.avalokan.ui.theme.PrimaryLight
import com.example.avalokan.ui.theme.PrimaryTeal
import com.example.avalokan.ui.theme.Spacing
import com.example.avalokan.ui.theme.StandardButtonShape
import com.example.avalokan.ui.theme.StandardCardShape
import com.example.avalokan.ui.theme.TextPrimary

@Composable
fun AvalokanHome(
    modifier: Modifier = Modifier,
    onStoryClick: (String) -> Unit = {},
    onPlaceClick: (String) -> Unit = {},
    onExploreEventsClick: () -> Unit = {},
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val places by viewModel.places.collectAsState()

    HomeContent(
        modifier = modifier,
        heroStoryId = viewModel.heroStoryId,
        places = places,
        onStoryClick = onStoryClick,
        onPlaceClick = onPlaceClick,
        onExploreEventsClick = onExploreEventsClick
    )
}

@Composable
private fun HomeContent(
    heroStoryId: String,
    places: List<PlaceItem>,
    onStoryClick: (String) -> Unit,
    onPlaceClick: (String) -> Unit,
    onExploreEventsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = Spacing.large),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(Spacing.small))
        TopIcons()
        StoryCard(
            modifier = Modifier.padding(horizontal = Spacing.sidePadding),
            onReadClick = { onStoryClick(heroStoryId) }
        )
        HistoricalGems(places = places, onPlaceClick = onPlaceClick)
        EventSuggestion(
            modifier = Modifier.padding(horizontal = Spacing.sidePadding),
            onExploreClick = onExploreEventsClick
        )
        RecentDiscoveries(
            modifier = Modifier.padding(horizontal = Spacing.sidePadding),
            places = places,
            onPlaceClick = onPlaceClick
        )
    }
}

@Composable
private fun TopIcons(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = Spacing.sidePadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = stringResource(R.string.namaste).uppercase(),
                style = MaterialTheme.typography.labelSmall,
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
                .size(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = stringResource(R.string.notifications),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun StoryCard(
    modifier: Modifier = Modifier,
    // API-ready: pass remote image URL/model here later; null = placeholder below
    imageUrl: String? = null,
    onReadClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = EditorialCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
            // Placeholder image container (correct size/shape for future API image).
            // Later: if (imageUrl != null) AsyncImage(imageUrl, ..., contentScale = Crop)
            // else placeholder below. No networking added yet.
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(Brush.verticalGradient(listOf(PrimaryTeal, TextPrimary)))
            )
            // Dark scrim so badge/title/button stay legible over any future photo
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(Brush.verticalGradient(0f to Color.Transparent, 0.4f to Color.Black.copy(0.7f)))
            )
            Surface(
                modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                shape = BadgeShape, color = AccentMarigold
            ) {
                Text(
                    text = stringResource(R.string.storyOfTheDay).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Column(
                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = "The Sacred Echoes of Boudhanath", style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp, lineHeight = 30.sp), color = Color.White, maxLines = 3, overflow = TextOverflow.Ellipsis)
                Text(text = "Discover the spiritual significance...", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(0.85f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(2.dp))
                Button(
                    onClick = onReadClick,
                    shape = BadgeShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = PrimaryTeal),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(text = stringResource(R.string.readStory), style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
private fun HistoricalGems(
    places: List<PlaceItem>,
    onPlaceClick: (String) -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.sidePadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.historicalGems), style = MaterialTheme.typography.titleMedium)
            Text(text = stringResource(R.string.viewAll), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = Spacing.sidePadding),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                count = places.size,
                key = { index -> places[index].id }
            ) { index ->
                val place = places[index]
                PlaceCard(
                    title = place.name,
                    subtitle = place.description,
                    badge = if (index == 0) "EST. 3D CENTURY" else "2500+ YRS",
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
        onCardClick = onCardClick
    )
}

@Composable
private fun RecentDiscoveries(
    modifier: Modifier = Modifier,
    places: List<PlaceItem> = emptyList(),
    onPlaceClick: (String) -> Unit = {}
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.small)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Recent Discoveries", style = MaterialTheme.typography.titleMedium)
            Text(text = stringResource(R.string.viewAll), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        }
        places.filter { it.id == "boudha-stupa" || it.id == "bhaktapur-pottery" }
            .forEach { place ->
                com.example.avalokan.ui.components.HeritageCard(
                    title = place.name,
                    subtitle = place.meta,
                    badge = if (place.id == "boudha-stupa") "NEW" else "TRENDING",
                    rating = place.rating,
                    onCardClick = { onPlaceClick(place.id) }
                )
            }
    }
}

@Composable
private fun EventSuggestion(
    modifier: Modifier = Modifier,
    onExploreClick: () -> Unit = {}
) {
    Card(modifier = modifier.fillMaxWidth(), shape = StandardCardShape, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = stringResource(R.string.localED), style = MaterialTheme.typography.titleMedium)
                Text(text = stringResource(R.string.joinEventDesc), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Button(onClick = onExploreClick, shape = StandardButtonShape, colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal, contentColor = Color.White)) {
                    Text(text = stringResource(R.string.exploreEB))
                }
            }
            Surface(shape = StandardCardShape, color = Color.White, shadowElevation = 4.dp) {
                Icon(Icons.Default.CalendarMonth, null, tint = PrimaryTeal, modifier = Modifier.padding(12.dp).size(28.dp))
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview(){
    AvalokanTheme() {
        HomeContent(
            heroStoryId = "boudhanath",
            places = listOf(
                PlaceItem("patan-durbar", "Patan Durbar Square", "Artistic heritage of Lalitpur.", "Lalitpur • Historical Site", "Historical", "4.8"),
                PlaceItem("swayambhu", "Swayambhu", "The Ancient Hill.", "Kathmandu • Spiritual Site", "Historical", "4.7"),
                PlaceItem("boudha-stupa", "Boudha Stupa", "Boudhanath • Spiritual Site.", "Boudhanath • Spiritual Site", "Cultural", "4.9"),
                PlaceItem("bhaktapur-pottery", "Bhaktapur Pottery Square", "Bhaktapur • Craft Quarter.", "Bhaktapur • Craft Quarter", "Cultural", "4.7")
            ),
            onStoryClick = {},
            onPlaceClick = {},
            onExploreEventsClick = {}
        )
    }
}