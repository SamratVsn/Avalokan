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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Schedule
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
import com.example.avalokan.ui.common.placeBadgeLabelRes
import com.example.avalokan.ui.components.PhotoOverlayIconButton
import com.example.avalokan.ui.components.PhotoOverlayOutlinedButton
import com.example.avalokan.ui.theme.AvalokanTheme
import com.example.avalokan.ui.theme.BadgeShape
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
    val visitedIds by viewModel.visitedIds.collectAsState()
    val isVisited = visitedIds.contains(place?.id)
    val markedVisitedMessage = stringResource(R.string.marked_as_visited)
    val removedVisitedMessage = stringResource(R.string.visited_removed)
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
            rating = place?.sourcedRating.orEmpty(),
            description = place?.description.orEmpty(),
            badge = place?.badge.orEmpty(),
            classificationBadges = place?.badges()
                ?.map { stringResource(placeBadgeLabelRes(it)) }
                .orEmpty(),
            entryFee = place?.entryFee.orEmpty(),
            bestTime = place?.bestTime.orEmpty(),
            hours = place?.hours.orEmpty(),
            sourceLabel = place?.sourceLabel.orEmpty(),
            sourceUrl = place?.sourceUrl.orEmpty(),
            lastVerifiedAt = place?.lastVerifiedAt.orEmpty(),
            showPracticalInfo = place?.canShowPracticalInfo == true,
            imageUrl = place?.imageUrl,
            isSaved = isSaved,
            isVisited = isVisited,
            onSaveClick = {
                if (viewModel.toggleSave()) {
                    scope.launch { snackbarHost.showSnackbar("Saved to Collection") }
                }
            },
            onVisitedClick = {
                val nowVisited = viewModel.toggleVisited()
                scope.launch {
                    snackbarHost.showSnackbar(
                        if (nowVisited) markedVisitedMessage else removedVisitedMessage
                    )
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
    classificationBadges: List<String> = emptyList(),
    entryFee: String = "",
    bestTime: String = "",
    hours: String = "",
    sourceLabel: String = "",
    sourceUrl: String = "",
    lastVerifiedAt: String = "",
    /** False hides every practical fact that has no recorded provenance. */
    showPracticalInfo: Boolean = false,
    imageUrl: String? = null,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    isVisited: Boolean = false,
    onVisitedClick: () -> Unit = {},
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
                                MaterialTheme.colorScheme.primaryContainer,
                                StandardCardShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Directions,
                            contentDescription = "Get directions",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier.size(48.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                StandardCardShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share this place",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Button(
                        onClick = onSaveClick,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSaved) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                            contentColor = if (isSaved) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onPrimary
                            }
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
                                .background(MaterialTheme.colorScheme.surfaceVariant),
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
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                        PhotoOverlayOutlinedButton(
                            text = "View Gallery",
                            icon = Icons.Default.PhotoLibrary,
                            onClick = {},
                            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
                        )
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
                            // Ratings appear only when [sourcedRating] supplied
                            // one. No badge, rather than "No rating yet", keeps
                            // an unrated place from looking like a scored one.
                            if (rating.isNotBlank()) {
                                DetailBadge(text = "$rating ★")
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (classificationBadges.isNotEmpty()) {
                                classificationBadges.forEach { label ->
                                    DetailBadge(text = label)
                                }
                            } else if (badge.isNotBlank()) {
                                DetailBadge(text = badge)
                            }
                        }
                        // Explicit visit tracking: the user must choose this;
                        // merely opening the detail screen is not a visit.
                        OutlinedButton(
                            onClick = onVisitedClick,
                            shape = BadgeShape
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = stringResource(
                                    if (isVisited) {
                                        R.string.visited_badge
                                    } else {
                                        R.string.mark_as_visited
                                    }
                                ),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                item {
                    // Practical facts are shown only with provenance. A blank
                    // value and an unverified value are both omitted; neither
                    // is turned into a "Not available" pseudo-fact.
                    val stats = buildList {
                        if (entryFee.isNotBlank()) {
                            add(StatUi(Icons.Default.ConfirmationNumber, "Entry Fee", entryFee))
                        }
                        if (bestTime.isNotBlank()) {
                            add(StatUi(Icons.Default.CalendarMonth, "Best Time", bestTime))
                        }
                        if (hours.isNotBlank()) {
                            add(StatUi(Icons.Default.Schedule, "Hours", hours))
                        }
                    }
                    val source = sourceLabel.ifBlank { sourceUrl }
                    if (showPracticalInfo && stats.isNotEmpty()) {
                        Column(
                            modifier = Modifier.padding(horizontal = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DetailSectionTitle(text = "Practical details")
                            stats.chunked(2).forEach { row ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    row.forEach { stat ->
                                        StatCard(stat, Modifier.weight(1f))
                                    }
                                    if (row.size == 1) {
                                        Spacer(Modifier.weight(1f))
                                    }
                                }
                            }
                            if (source.isNotBlank() || lastVerifiedAt.isNotBlank()) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    if (source.isNotBlank()) {
                                        Text(
                                            text = stringResource(R.string.place_info_source, source),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (lastVerifiedAt.isNotBlank()) {
                                        Text(
                                            text = stringResource(
                                                R.string.place_info_last_verified,
                                                lastVerifiedAt
                                            ),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
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
            PhotoOverlayIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(top = 8.dp, start = 16.dp)
            )
        }
    }
}

@Composable
private fun StatCard(stat: StatUi, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
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
            Icon(
                stat.icon,
                null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
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

@Preview(name = "Place Detail — Light")
@Preview(
    name = "Place Detail — Dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PlaceDetailPreview() {
    AvalokanTheme {
        PlaceDetailContent(
            title = "Kathmandu Durbar Square",
            rating = "4.8",
            description = "Once the royal palace of the Malla kings.",
            badge = "UNESCO Site",
            entryFee = "Rs. 1,000 (foreign visitors)",
            hours = "9 AM – 5 PM",
            sourceLabel = "Department of Archaeology",
            lastVerifiedAt = "2026-10-01",
            showPracticalInfo = true,
            isSaved = false,
            onSaveClick = {},
            onBackClick = {},
            onShareClick = {},
            onDirectionsClick = {},
            snackbarHost = SnackbarHostState()
        )
    }
}
