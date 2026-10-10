package com.example.avalokan.ui.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.avalokan.ui.theme.AccentMarigold
import com.example.avalokan.ui.theme.AvalokanTheme
import com.example.avalokan.ui.theme.BadgeShape
import com.example.avalokan.ui.theme.PrimaryLight
import com.example.avalokan.ui.theme.PrimaryTeal
import com.example.avalokan.ui.theme.Spacing
import com.example.avalokan.ui.theme.StandardCardShape

private data class StatUi(val icon: ImageVector, val label: String, val value: String)

private val PlaceStats = listOf(
    StatUi(Icons.Default.ConfirmationNumber, "Entry Fee", "Rs. 100"),
    StatUi(Icons.Default.CalendarMonth, "Best Time", "Oct – Mar"),
    StatUi(Icons.Default.Schedule, "Hours", "9 AM – 5 PM"),
    StatUi(Icons.Default.Star, "Rating", "4.8")
)

@Composable
fun PlaceDetailScreen(
    placeId: String = "preview",
    onBackClick: () -> Unit = {},
    viewModel: PlaceDetailViewModel = hiltViewModel()
) {
    val place by viewModel.place.collectAsState()
    val context = LocalContext.current
    val placeTitle = place?.name ?: "Kathmandu Durbar Square"
    // Location comes from the place itself ("Lalitpur • Historical Site"),
    // never a hardcoded city — places can be anywhere in Nepal.
    val placeLocation = place?.meta?.substringBefore("•")?.trim()
        ?.ifBlank { "Nepal" } ?: "Nepal"
    val onShareClick = {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                "Check out this heritage site: $placeTitle on Nepal Heritage Explorer!"
            )
        }
        context.startActivity(Intent.createChooser(intent, "Share place"))
    }
    val onDirectionsClick = {
        val query = Uri.encode("$placeTitle, $placeLocation, Nepal")
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$query")))
    }
    PlaceDetailContent(
        title = placeTitle,
        rating = place?.rating?.ifBlank { "4.8" } ?: "4.8",
        description = place?.description ?: "Once the royal palace of the Malla kings, Kathmandu Durbar Square packs centuries of Newari art, temples, and courtyards into one plaza. Don't miss the Kumari Ghar, Taleju Temple, and the morning pigeon-dotted courtyards before the crowds arrive.",
        onBackClick = onBackClick,
        onShareClick = onShareClick,
        onDirectionsClick = onDirectionsClick
    )
}

@Composable
private fun PlaceDetailContent(
    title: String,
    rating: String,
    description: String,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onDirectionsClick: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            // Sticky footer: directions + save
            Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDirectionsClick,
                        modifier = Modifier.size(48.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                StandardCardShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Directions,
                            contentDescription = "Get directions",
                            tint = PrimaryTeal
                        )
                    }
                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier.size(48.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                StandardCardShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share this place",
                            tint = PrimaryTeal
                        )
                    }
                    Button(
                        onClick = {},
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                    ) {
                        Text(text = "Save to Collection")
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Gallery header with back + View Gallery (placeholder until Data image)
                Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .background(PrimaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Landscape,
                            contentDescription = null,
                            tint = PrimaryTeal,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.align(Alignment.TopStart)
                            .statusBarsPadding()
                            .padding(top = 8.dp, start = 16.dp)
                            .size(40.dp)
                            .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
                    ) {
                        Icon(
                            Icons.Default.PhotoLibrary,
                            null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(text = "View Gallery")
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.weight(1f)
                        )
                        DetailBadge(text = "$rating ★")
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailBadge(text = "UNESCO Site")
                        DetailBadge(
                            text = "Open Daily",
                            containerColor = AccentMarigold.copy(alpha = 0.12f),
                            contentColor = AccentMarigold
                        )
                    }
                }
            }
            item {
                // 2x2 quick-stats grid (fixed height inside LazyColumn)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxWidth()
                        .height(220.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    userScrollEnabled = false
                ) {
                    items(PlaceStats) { stat ->
                        StatCard(stat)
                    }
                }
            }
            item {
                ExpandableDescription(
                    description = description,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item {
                // Book-a-guide teal card with availability status
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = StandardCardShape,
                    colors = CardDefaults.cardColors(containerColor = PrimaryTeal)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(48.dp)
                                .background(
                                    Color.White.copy(alpha = 0.15f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AccessTime,
                                null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(shape = BadgeShape, color = Color.White.copy(alpha = 0.2f)) {
                                Text(
                                    text = "● Guide Available",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Book a Heritage Guide",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Text(
                                text = "Local experts, 2-hr walk • Rs. 800",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(stat: StatUi) {
    Card(
        shape = StandardCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(stat.icon, null, tint = PrimaryTeal, modifier = Modifier.size(22.dp))
            Text(
                text = stat.label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stat.value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ExpandableDescription(description: String, modifier: Modifier = Modifier) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        DetailSectionTitle(text = "About this Place")
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
            maxLines = if (expanded) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = if (expanded) "Show less" else "Read more",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = PrimaryTeal,
            modifier = Modifier.clickable { expanded = !expanded }
        )
    }
}

@Preview
@Composable
private fun PlaceDetailPreview() {
    AvalokanTheme {
        PlaceDetailContent(
            title = "Kathmandu Durbar Square",
            rating = "4.8",
            description = "Once the royal palace of the Malla kings.",
            onBackClick = {},
            onShareClick = {},
            onDirectionsClick = {}
        )
    }
}
