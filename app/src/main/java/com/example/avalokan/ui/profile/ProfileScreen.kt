package com.example.avalokan.ui.profile

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ConfirmationNumber
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.avalokan.R
import com.example.avalokan.ui.theme.AvalokanTheme
import com.example.avalokan.ui.theme.EditorialCardShape
import com.example.avalokan.ui.theme.PrimaryLight
import com.example.avalokan.ui.theme.PrimaryTeal
import com.example.avalokan.ui.theme.Spacing
import com.example.avalokan.ui.theme.StandardCardShape

@Composable
fun ProfileScreen(
    onSettingsClick: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val userName by viewModel.userName.collectAsState()
    val bio by viewModel.bio.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val avatarUri by viewModel.avatarUri.collectAsState()

    ProfileContent(
        userName = userName,
        bio = bio,
        stats = stats,
        avatarUri = avatarUri,
        onSettingsClick = onSettingsClick
    )
}

@Composable
private fun ProfileContent(
    userName: String,
    bio: String,
    stats: ProfileStats,
    avatarUri: String?,
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
            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.sidePadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.profile),
                style = MaterialTheme.typography.headlineMedium
            )
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
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
            modifier = Modifier.padding(horizontal = Spacing.sidePadding)
        )
        HeritageCollection()
        UpcomingRegistrations(modifier = Modifier.padding(horizontal = Spacing.sidePadding))
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
        shape = EditorialCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
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
                    shadowElevation = 2.dp
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
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = bio,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun HeritageCollection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.sidePadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.myHeritageColl),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(R.string.manage),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.sidePadding),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CollectionCards(
                "Durbar Square",
                "Kathmandu",
                modifier = Modifier.weight(1f)
            )
            CollectionCards(
                "Boudha Stupa",
                "Kathmandu",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CollectionCards(place: String, loc: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Card(shape = StandardCardShape) {
            Box(
                modifier = Modifier.fillMaxWidth().height(150.dp).background(PrimaryLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Landscape, null, tint = PrimaryTeal)
            }
        }
        Text(
            text = place,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = loc,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun UpcomingRegistrations(modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(R.string.upcomingReg),
            style = MaterialTheme.typography.titleMedium
        )
        RegisteredBox()
    }
}

@Composable
private fun RegisteredBox() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = StandardCardShape,
        colors = CardDefaults.cardColors(containerColor = PrimaryTeal)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(shape = StandardCardShape, color = Color.White.copy(alpha = 0.15f)) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SEP",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                    Text(
                        text = "17",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Indra Jatra Festival",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Basantapur Durbar Square • 12:00 PM",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.15f)) {
                Icon(
                    Icons.Default.ConfirmationNumber,
                    null,
                    tint = Color.White,
                    modifier = Modifier.padding(8.dp).size(20.dp)
                )
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
            onSettingsClick = {}
        )
    }
}
