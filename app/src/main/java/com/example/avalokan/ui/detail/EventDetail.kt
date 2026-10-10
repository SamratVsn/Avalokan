package com.example.avalokan.ui.detail

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.avalokan.ui.theme.AvalokanTheme
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

private val ExperienceCardBg = Color(0xFFF9F9F9)

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
    val eventTitle = event?.title ?: "Indra Jatra 2024: The Chariot Procession"
    val onShareClick = {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Check out this heritage event: $eventTitle on Avalokan, a Nepal Heritage Explorer!")
        }
        context.startActivity(Intent.createChooser(intent, "Share event"))
    }
    EventDetailContent(
        title = event?.title ?: "Indra Jatra 2024: The Chariot Procession",
        location = event?.meta?.substringBefore("•")?.trim()
            ?.ifBlank { "Basantapur Durbar Square" }
            ?: "Basantapur Durbar Square",
        isFavorite = isFavorite,
        onFavoriteClick = { isFavorite = !isFavorite },
        onShareClick = onShareClick,
        onBookClick = { scope.launch { snackbarHost.showSnackbar("Booked! See you there.") } },
        snackbarHost = snackbarHost,
        onBackClick = onBackClick
    )
}

@Composable
private fun EventDetailContent(
    title: String,
    location: String,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit,
    onBookClick: () -> Unit,
    snackbarHost: SnackbarHostState,
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
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
                            text = "Rs. 500",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    Button(
                        onClick = onBookClick,
                        modifier = Modifier.height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Text(text = "Book Now")
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            Icons.Default.Person,
                            null,
                            modifier = Modifier.size(18.dp)
                        )
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
                Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = PrimaryTeal,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .statusBarsPadding()
                            .padding(top = 8.dp, start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.size(40.dp)
                                .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = onShareClick,
                                modifier = Modifier.size(40.dp)
                                    .background(Color.Black.copy(alpha = 0.35f), CircleShape)
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
                                    .background(Color.Black.copy(alpha = 0.35f), CircleShape)
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
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    DetailBadge(text = "Sept 17 • Festival")
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
            item {
                // Date / Time / Location metadata list
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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
            }
            item {
                // Experience card: neutral #F9F9F9, checklist + facepile
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = StandardCardShape,
                    colors = CardDefaults.cardColors(containerColor = ExperienceCardBg)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Exclusive Heritage Tour",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        listOf(
                            "Guided old-city walk at dawn",
                            "Traditional Samay Baji tasting",
                            "Masked-dance viewing terrace"
                        ).forEach { point ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = PrimaryTeal,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = point,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                }
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
            isFavorite = false,
            onFavoriteClick = {},
            onShareClick = {},
            onBookClick = {},
            snackbarHost = SnackbarHostState(),
            onBackClick = {}
        )
    }
}
