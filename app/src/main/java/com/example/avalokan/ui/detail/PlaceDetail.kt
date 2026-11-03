package com.example.avalokan.ui.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.avalokan.R
import com.example.avalokan.ui.theme.AccentMarigold
import com.example.avalokan.ui.theme.AvalokanTheme
import com.example.avalokan.ui.theme.BadgeShape
import com.example.avalokan.ui.theme.PrimaryLight
import com.example.avalokan.ui.theme.PrimaryTeal
import com.example.avalokan.ui.theme.Spacing
import com.example.avalokan.ui.theme.StandardCardShape

private data class StatUi(val icon: ImageVector, val label: String, val value: String)

@Composable
fun PlaceDetailScreen(
    placeId: String = "preview",
    onBackClick: () -> Unit = {},
    viewModel: PlaceDetailViewModel = hiltViewModel()
) {
    val place by viewModel.place.collectAsState()
    val context = LocalContext.current
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val savedIds by viewModel.savedIds.collectAsState()
    val isSaved = savedIds.contains(place?.id)
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                PlaceDetailEvent.NavigateBack -> onBackClick()
            }
        }
    }

    val placeTitle = place?.name.orEmpty()
    // Location comes from the place itself ("Lalitpur • Historical Site"),
    // never a hardcoded city — places can be anywhere in Nepal.
    val placeLocation = place?.meta?.substringBefore("•")?.trim().orEmpty()
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
        val query = Uri.encode(
            listOf(placeTitle, placeLocation, "Nepal")
                .filter { it.isNotBlank() }
                .joinToString(", ")
        )
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$query")))
    }
    DetailBody(
        itemPresent = place != null,
        kind = "place",
        onBackClick = onBackClick
    ) {
        PlaceDetailContent(
            title = placeTitle,
            rating = place?.rating.orEmpty(),
            description = place?.description.orEmpty(),
            badge = place?.badge.orEmpty(),
            entryFee = place?.entryFee.orEmpty(),
            bestTime = place?.bestTime.orEmpty(),
            hours = place?.hours.orEmpty(),
            imageUrl = place?.imageUrl,
            isSaved = isSaved,
            onSaveClick = {
                if (viewModel.toggleSave()) {
                    scope.launch { snackbarHost.showSnackbar("Saved to Collection") }
                }
            },
            onBackClick = onBackClick,
            onShareClick = onShareClick,
            onDirectionsClick = onDirectionsClick,
            snackbarHost = snackbarHost
        )
    }
}

@Composable
private fun PlaceDetailContent(
    title: String,
    rating: String,
    description: String,
    badge: String = "",
    entryFee: String = "",
    bestTime: String = "",
    hours: String = "",
    imageUrl: String? = null,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onDirectionsClick: () -> Unit,
    snackbarHost: SnackbarHostState
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(hostState = snackbarHost) },
        bottomBar = {
            // Sticky footer: directions + save
            Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDirectionsClick,
                        modifier = Modifier.size(48.dp)
                            .background(
                                PrimaryLight,
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
                                PrimaryLight,
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
                        onClick = onSaveClick,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSaved) MaterialTheme.colorScheme.surfaceVariant else PrimaryTeal,
                            contentColor = if (isSaved) PrimaryTeal else Color.White
                        )
                    ) {
                        Text(
                            text = if (isSaved) "Saved ✓" else "Save to Collection",
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Gallery header (AsyncImage fills it when the Data layer has a URL)
                    Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                                .background(PrimaryLight),
                            contentAlignment = Alignment.Center
                        ) {
                            if (imageUrl != null) {
                                coil.compose.AsyncImage(
                                    model = imageUrl,
                                    contentDescription = title,
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    Icons.Default.Landscape,
                                    contentDescription = null,
                                    tint = PrimaryTeal,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
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
                        modifier = Modifier.padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = title.ifBlank { stringResource(R.string.untitled) },
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.weight(1f)
                            )
                            if (rating.isNotBlank()) {
                                DetailBadge(text = "$rating ★")
                            } else {
                                DetailBadge(text = stringResource(R.string.no_rating))
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (badge.isNotBlank()) {
                                DetailBadge(text = badge)
                            }
                        }
                    }
                }
                item {
                    // 2x2 quick-stats grid, with an explicit notice for missing values
                    val notAvailable = stringResource(R.string.not_available)
                    val stats = listOf(
                        StatUi(Icons.Default.ConfirmationNumber, "Entry Fee", entryFee.ifBlank { notAvailable }),
                        StatUi(Icons.Default.CalendarMonth, "Best Time", bestTime.ifBlank { notAvailable }),
                        StatUi(Icons.Default.Schedule, "Hours", hours.ifBlank { notAvailable }),
                        StatUi(Icons.Default.Star, "Rating", rating.ifBlank { stringResource(R.string.no_rating) })
                    )
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxWidth()
                            .height(220.dp)
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        userScrollEnabled = false
                    ) {
                        items(stats) { stat ->
                            StatCard(stat)
                        }
                    }
                }
                item {
                    DescriptionSection(
                        description = description,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(top = 8.dp, start = 16.dp)
                    .size(40.dp)
                    .background(Color.Black.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun StatCard(stat: StatUi) {
    Card(
        shape = StandardCardShape,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(stat.icon, null, tint = PrimaryTeal, modifier = Modifier.size(22.dp))
            Text(
                text = stat.label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                ),
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
private fun DescriptionSection(description: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        DetailSectionTitle(text = "About this Place")
        Text(
            text = description.ifBlank { stringResource(R.string.no_description) },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
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
            badge = "UNESCO Site",
            isSaved = false,
            onSaveClick = {},
            onBackClick = {},
            onShareClick = {},
            onDirectionsClick = {},
            snackbarHost = SnackbarHostState()
        )
    }
}
