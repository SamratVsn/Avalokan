package com.example.avalokan.ui.detail

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.avalokan.ui.theme.AvalokanTheme
import com.example.avalokan.ui.theme.HeroScrim
import com.example.avalokan.ui.theme.PrimaryTeal
import com.example.avalokan.ui.theme.StandardCardShape
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@Composable
fun EventDetailScreen(
    eventId: String = "preview",
    onBackClick: () -> Unit = {},
    viewModel: EventDetailViewModel = hiltViewModel()
) {
    val event by viewModel.event.collectAsState()
    val context = LocalContext.current
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isFavorite by rememberSaveable { mutableStateOf(false) }
    val eventTitle = event?.title.orEmpty()
    val eventType = EventType.from(event?.type)
    val onShareClick = {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Check out this heritage event: $eventTitle on Avalokan, a Nepal Heritage Explorer!")
        }
        context.startActivity(Intent.createChooser(intent, "Share event"))
    }
    DetailBody(
        itemPresent = event != null,
        kind = "event",
        onBackClick = onBackClick
    ) {
        EventDetailContent(
            title = event?.title.orEmpty(),
            location = event?.meta?.substringBefore("•")?.trim()
                ?.ifBlank { "Nepal" } ?: "Nepal",
            eventType = eventType,
            date = event?.date.orEmpty(),
            description = event?.description.orEmpty(),
            fee = event?.fee?.ifBlank { "Rs. 500" } ?: "Rs. 500",
            imageUrl = event?.imageUrl,
            isFavorite = isFavorite,
            onFavoriteClick = { isFavorite = !isFavorite },
            onShareClick = onShareClick,
            onBookClick = { scope.launch { snackbarHost.showSnackbar("Booked! See you there.") } },
            snackbarHost = snackbarHost,
            onBackClick = onBackClick
        )
    }
}

@Composable
private fun EventDetailContent(
    title: String,
    location: String,
    eventType: EventType,
    date: String,
    description: String,
    fee: String,
    imageUrl: String? = null,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit,
    onBookClick: () -> Unit,
    snackbarHost: SnackbarHostState,
    onBackClick: () -> Unit
) {
    val accent = eventType.accent
    Scaffold(
        // Matches the white sheet so no color gap appears at the bottom.
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(hostState = snackbarHost) },
        bottomBar = {
            // Sticky footer: price + Book Now with shadow
            Surface(shadowElevation = 12.dp, color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Price per person",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = fee,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    Button(
                        onClick = onBookClick,
                        modifier = Modifier.height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accent),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Text(text = "Book Now")
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    // 350dp hero; the white sheet below slides 30dp over it.
                    Box(modifier = Modifier.fillMaxWidth().height(350.dp)) {
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
                                    Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = PrimaryTeal,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .statusBarsPadding()
                                .padding(top = 8.dp, start = 16.dp, end = 16.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(
                                    onClick = onShareClick,
                                    modifier = Modifier.size(40.dp)
                                        .background(HeroScrim, CircleShape)
                                ) {
                                    Icon(
                                        Icons.Default.Share,
                                        contentDescription = "Share",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = onFavoriteClick,
                                    modifier = Modifier.size(40.dp)
                                        .background(HeroScrim, CircleShape)
                                ) {
                                    Icon(
                                        if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = if (isFavorite) "Remove from favorites" else "Save to favorites",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    // Layered sheet overlapping the hero by 30dp, 40dp top rounding.
                    Surface(
                        modifier = Modifier.fillMaxWidth()
                            .offset(y = (-30).dp)
                            .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 24.dp)
                                .padding(top = 24.dp, bottom = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                DetailBadge(
                                    text = if (date.isBlank()) "Festival" else "$date • Festival",
                                    containerColor = accent.copy(alpha = 0.12f),
                                    contentColor = accent,
                                    icon = eventBadgeIcon(eventType)
                                )
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            // Date / Time / Location metadata list
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                MetaRow(
                                    icon = Icons.Default.CalendarMonth,
                                    label = "Date",
                                    value = "Sept 17, 2024"
                                )
                                MetaRow(
                                    icon = Icons.Default.Schedule,
                                    label = "Time",
                                    value = "10:00 AM onwards"
                                )
                                MetaRow(
                                    icon = Icons.Default.LocationOn,
                                    label = "Location",
                                    value = location
                                )
                            }
                            // About the festival: full Firestore description.
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "About the Festival",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = description,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(top = 8.dp, start = 16.dp)
                    .size(40.dp)
                    .background(HeroScrim, CircleShape)
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

@Preview
@Composable
private fun EventDetailPreview() {
    AvalokanTheme {
        EventDetailContent(
            title = "Indra Jatra 2024: The Chariot Procession",
            location = "Basantapur Durbar Square",
            eventType = EventType.RELIGIOUS,
            date = "Sept 17, 2024",
            description = "The biggest religious street festival in Kathmandu.",
            fee = "Rs. 500",
            isFavorite = false,
            onFavoriteClick = {},
            onShareClick = {},
            onBookClick = {},
            snackbarHost = SnackbarHostState(),
            onBackClick = {}
        )
    }
}
