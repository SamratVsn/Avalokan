package com.example.avalokan.ui.detail

import android.content.Intent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.avalokan.R
import com.example.avalokan.domain.classification.Participation
import com.example.avalokan.domain.model.Event
import com.example.avalokan.domain.model.EventCta
import com.example.avalokan.domain.model.aboutTitle
import com.example.avalokan.domain.model.cta
import com.example.avalokan.domain.model.priceDisplay
import com.example.avalokan.ui.common.aboutTitleRes
import com.example.avalokan.ui.common.ctaLabelRes
import com.example.avalokan.ui.common.dataOrNull
import com.example.avalokan.ui.common.eventKindLabelRes
import com.example.avalokan.ui.common.eventPriceValue
import com.example.avalokan.ui.common.openUrlInBrowser
import com.example.avalokan.ui.common.participationLabelRes
import com.example.avalokan.ui.common.priceLabelRes
import com.example.avalokan.ui.common.scopeLabelRes
import com.example.avalokan.ui.components.PhotoOverlayIconButton
import com.example.avalokan.ui.theme.AvalokanTheme
import kotlinx.coroutines.launch

@Composable
fun EventDetailScreen(
    eventId: String = "preview",
    onBackClick: () -> Unit = {},
    viewModel: EventDetailViewModel = hiltViewModel()
) {
    val eventState by viewModel.eventState.collectAsState()
    val event = eventState.dataOrNull
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val context = LocalContext.current
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val isFavorite = event?.id?.let { favoriteIds.contains(it) } == true
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                EventDetailEvent.NavigateBack -> onBackClick()
            }
        }
    }

    val linkUnavailable = stringResource(R.string.booking_unavailable)
    DetailBody(
        state = eventState,
        kind = "event",
        onBackClick = onBackClick,
        onRetry = viewModel::retry
    ) { loadedEvent ->
        val shareTitle = loadedEvent.title.ifBlank { stringResource(R.string.untitled) }
        val onShareClick = {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Check out this heritage event: $shareTitle on Avalokan, a Nepal Heritage Explorer!"
                )
            }
            context.startActivity(Intent.createChooser(intent, "Share event"))
        }
        EventDetailContent(
            event = loadedEvent,
            eventType = EventType.from(loadedEvent.type, loadedEvent.primaryKind),
            isFavorite = isFavorite,
            onFavoriteClick = { viewModel.toggleFavorite() },
            onShareClick = onShareClick,
            onOpenLink = {
                if (!context.openUrlInBrowser(loadedEvent.joinLink)) {
                    scope.launch { snackbarHost.showSnackbar(linkUnavailable) }
                }
            },
            snackbarHost = snackbarHost,
            onBackClick = onBackClick
        )
    }
}

@Composable
private fun EventDetailContent(
    event: Event,
    eventType: EventType,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit,
    onOpenLink: () -> Unit,
    snackbarHost: SnackbarHostState,
    onBackClick: () -> Unit
) {
    val badgeContainer = eventType.badgeContainerColor()
    val badgeContent = eventType.badgeContentColor()
    val actionContainer = eventType.actionContainerColor()
    val actionContent = eventType.actionContentColor()

    val title = event.title
    val location = event.meta.substringBefore("•").trim()
    val date = event.date
    val scopeText = stringResource(scopeLabelRes(event.geographicScope))
    val kindText = stringResource(eventKindLabelRes(event.primaryKind))
    val price = event.priceDisplay()
    val cta = event.cta()
    val ctaRes = ctaLabelRes(cta)
    val showFooter = price != null || ctaRes != null

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(hostState = snackbarHost) },
        bottomBar = {
            if (showFooter) {
                Surface(shadowElevation = 12.dp, color = MaterialTheme.colorScheme.surface) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = if (price == null) {
                            Arrangement.End
                        } else {
                            Arrangement.spacedBy(12.dp)
                        }
                    ) {
                        if (price != null) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(priceLabelRes(price.label)),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = eventPriceValue(price.label, price.value),
                                    style = MaterialTheme.typography.headlineMedium
                                )
                            }
                        }
                        if (ctaRes != null) {
                            Button(
                                onClick = onOpenLink,
                                modifier = Modifier.height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = actionContainer,
                                    contentColor = actionContent
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                            ) {
                                Text(text = stringResource(ctaRes))
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
                            if (event.imageUrl != null) {
                                coil.compose.AsyncImage(
                                    model = event.imageUrl,
                                    contentDescription = title,
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
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
                                PhotoOverlayIconButton(
                                    icon = Icons.Default.Share,
                                    contentDescription = "Share",
                                    onClick = onShareClick
                                )
                                PhotoOverlayIconButton(
                                    icon = if (isFavorite) {
                                        Icons.Default.Favorite
                                    } else {
                                        Icons.Default.FavoriteBorder
                                    },
                                    contentDescription = if (isFavorite) {
                                        "Remove from favorites"
                                    } else {
                                        "Save to favorites"
                                    },
                                    onClick = onFavoriteClick
                                )
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
                                    text = date.ifBlank { stringResource(R.string.no_date) },
                                    containerColor = badgeContainer,
                                    contentColor = badgeContent,
                                    icon = eventBadgeIcon(eventType)
                                )
                                Text(
                                    text = title.ifBlank { stringResource(R.string.untitled) },
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            // Verified metadata. Location is the venue; reach is
                            // the geographical scope, and they are shown apart.
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                MetaRow(
                                    icon = Icons.Default.CalendarMonth,
                                    label = "Date",
                                    value = date.ifBlank { stringResource(R.string.no_date) }
                                )
                                MetaRow(
                                    icon = Icons.Default.LocationOn,
                                    label = "Location",
                                    value = location.ifBlank { stringResource(R.string.no_location) }
                                )
                                MetaRow(
                                    icon = eventBadgeIcon(eventType),
                                    label = "Type",
                                    value = kindText
                                )
                                MetaRow(
                                    icon = Icons.Default.LocationOn,
                                    label = stringResource(R.string.event_reach),
                                    value = scopeText
                                )
                                if (event.participationRequirement != Participation.UNKNOWN) {
                                    MetaRow(
                                        icon = Icons.Default.Person,
                                        label = stringResource(R.string.event_participation),
                                        value = stringResource(
                                            participationLabelRes(event.participationRequirement)
                                        )
                                    )
                                }
                            }
                            // Event-specific section title, never a generic
                            // "About the Festival" for a workshop or tech event.
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = stringResource(aboutTitleRes(event.aboutTitle())),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = event.description.ifBlank {
                                        stringResource(R.string.no_description)
                                    },
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
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

private val PreviewEvent = Event(
    id = "preview",
    title = "Indra Jatra",
    description = "The biggest religious street festival in Kathmandu.",
    meta = "Basantapur Durbar Square • Sept 17",
    fee = "Free",
    type = "Cultural",
    date = "Sept 17, 2026",
    eventKind = "FESTIVAL",
    locality = "LOCAL",
    ticketing = "FREE",
    participation = "OPEN",
    format = "FESTIVAL",
)

@Preview(name = "Event Detail — Light")
@Preview(
    name = "Event Detail — Dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun EventDetailPreview() {
    AvalokanTheme {
        EventDetailContent(
            event = PreviewEvent,
            eventType = EventType.CULTURAL,
            isFavorite = false,
            onFavoriteClick = {},
            onShareClick = {},
            onOpenLink = {},
            snackbarHost = SnackbarHostState(),
            onBackClick = {}
        )
    }
}
