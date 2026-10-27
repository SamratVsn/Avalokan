package com.example.avalokan.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.avalokan.R
import com.example.avalokan.data.event.EventItem
import com.example.avalokan.data.place.PlaceItem
import androidx.compose.material.icons.filled.CalendarMonth
import com.example.avalokan.ui.theme.AvalokanTheme
import com.example.avalokan.ui.theme.GreyLight
import com.example.avalokan.ui.theme.PrimaryLight
import com.example.avalokan.ui.theme.PrimaryTeal
import com.example.avalokan.ui.theme.Spacing
import com.example.avalokan.ui.theme.StandardCardShape
import com.example.avalokan.ui.theme.TextPrimary
import com.example.avalokan.ui.theme.TextSecondary

@Composable
fun ProfileScreen(
    onSettingsClick: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val userName by viewModel.userName.collectAsState()
    val bio by viewModel.bio.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val avatarUri by viewModel.avatarUri.collectAsState()
    val savedPlaces by viewModel.savedPlaces.collectAsState()
    val favoriteEvents by viewModel.favoriteEvents.collectAsState()

    ProfileContent(
        userName = userName,
        bio = bio,
        stats = stats,
        avatarUri = avatarUri,
        savedPlaces = savedPlaces,
        favoriteEvents = favoriteEvents,
        onSettingsClick = onSettingsClick
    )
}

@Composable
private fun ProfileContent(
    userName: String,
    bio: String,
    stats: ProfileStats,
    avatarUri: String?,
    savedPlaces: List<PlaceItem>,
    favoriteEvents: List<EventItem>,
    onSettingsClick: () -> Unit
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
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.size(40.dp)
                    .background(
                        color = GreyLight,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        UserSection(
            userName = userName,
            bio = bio,
            stats = stats,
            avatarUri = avatarUri,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        HeritageCollection(savedPlaces = savedPlaces)
        FavoriteEvents(
            favoriteEvents = favoriteEvents,
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
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(40.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryLight)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    shape = CircleShape,
                    color = PrimaryLight,
                    shadowElevation = 4.dp,
                    border = BorderStroke(2.dp, Color.White)
                ) {
                    if (avatarUri != null) {
                        AsyncImage(
                            model = avatarUri,
                            contentDescription = "Profile photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(72.dp).clip(CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier.size(72.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userName.firstOrNull()?.uppercase() ?: "G",
                                style = MaterialTheme.typography.headlineMedium,
                                color = PrimaryTeal
                            )
                        }
                    }
                }
                Surface(
                    shape = CircleShape,
                    color = PrimaryTeal,
                    modifier = Modifier.padding(bottom = 4.dp, end = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Check,
                        null,
                        tint = Color.White,
                        modifier = Modifier.padding(4.dp).size(12.dp)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = userName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = bio,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                UserSectionInfo(stats.visited, "VISITED")
                UserSectionInfo(stats.saved, "SAVED")
                UserSectionInfo(stats.events, "EVENTS")
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
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                letterSpacing = 1.sp
            ),
            color = TextSecondary
        )
    }
}

@Composable
private fun HeritageCollection(savedPlaces: List<PlaceItem>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.savedPlaces),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(R.string.manage),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        if (savedPlaces.isEmpty()) {
            Text(
                text = "No saved places yet — tap the bookmark on any site.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                savedPlaces.take(2).forEach { place ->
                    CollectionCards(
                        place.name,
                        place.meta.substringBefore("•").trim().ifBlank { "Nepal" },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (savedPlaces.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CollectionCards(place: String, loc: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Card(shape = RoundedCornerShape(28.dp)) {
            Box(
                modifier = Modifier.fillMaxWidth().height(150.dp).background(PrimaryLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Landscape, null, tint = PrimaryTeal)
            }
        }
        Text(
            text = place,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = loc,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun FavoriteEvents(
    favoriteEvents: List<EventItem>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(R.string.favoriteEvents),
            style = MaterialTheme.typography.titleMedium
        )
        if (favoriteEvents.isEmpty()) {
            Text(
                text = stringResource(R.string.favoriteEventsEmpty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            favoriteEvents.forEach { event ->
                FavoriteEventRow(event)
            }
        }
    }
}

@Composable
private fun FavoriteEventRow(event: EventItem) {
    Card(
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
                    .clip(RoundedCornerShape(16.dp))
                    .background(PrimaryLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CalendarMonth, null, tint = PrimaryTeal)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = event.meta.substringBefore("•").trim().ifBlank { "Nepal" },
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (event.date.isNotBlank()) {
                    Text(
                        text = event.date,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ProfilePreview() {
    AvalokanTheme {
        ProfileContent(
            userName = "Guest",
            bio = "Exploring the heritage of Nepal",
            stats = ProfileStats(),
            avatarUri = null,
            savedPlaces = emptyList(),
            favoriteEvents = emptyList(),
            onSettingsClick = {}
        )
    }
}
