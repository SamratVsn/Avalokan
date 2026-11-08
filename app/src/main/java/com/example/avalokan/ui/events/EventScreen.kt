package com.example.avalokan.ui.events

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.avalokan.R
import com.example.avalokan.domain.classification.EventKind
import com.example.avalokan.domain.classification.GeographicScope
import com.example.avalokan.domain.model.Event
import com.example.avalokan.domain.model.EventCta
import com.example.avalokan.domain.model.cta
import com.example.avalokan.domain.model.priceDisplay
import com.example.avalokan.ui.common.ContentState
import com.example.avalokan.ui.common.cardActionLabelRes
import com.example.avalokan.ui.common.ctaLabelRes
import com.example.avalokan.ui.common.dataOrNull
import com.example.avalokan.ui.common.eventKindLabelRes
import com.example.avalokan.ui.common.eventPriceValue
import com.example.avalokan.ui.common.openUrlInBrowser
import com.example.avalokan.ui.common.scopeLabelRes
import com.example.avalokan.ui.components.EventsLoadingState
import com.example.avalokan.ui.components.StateMessage
import com.example.avalokan.ui.theme.AvalokanTheme
import com.example.avalokan.ui.theme.BadgeShape
import com.example.avalokan.ui.theme.GreyBorder
import com.example.avalokan.ui.theme.PrimaryGreen
import com.example.avalokan.ui.theme.PrimaryGreenDark
import com.example.avalokan.ui.theme.SageSoft
import com.example.avalokan.ui.theme.Spacing
import com.example.avalokan.ui.theme.StandardCardShape
import com.example.avalokan.ui.theme.Terracotta
import com.example.avalokan.ui.theme.TextOnDark

@Composable
fun EventScreen(
    onEventClick: (String) -> Unit = {},
    onExplorePlacesClick: () -> Unit = {},
    viewModel: EventsViewModel = hiltViewModel()
) {
    val contentState by viewModel.contentState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = viewModel::refresh,
        modifier = Modifier.fillMaxSize()
    ) {
        EventContent(
            contentState = contentState,
            onEventClick = onEventClick,
            onRetry = viewModel::retry,
            onExplorePlacesClick = onExplorePlacesClick
        )
    }
}

@Composable
private fun EventContent(
    contentState: ContentState<List<Event>>,
    onEventClick: (String) -> Unit,
    onRetry: () -> Unit,
    onExplorePlacesClick: () -> Unit
) {
    val events = contentState.dataOrNull.orEmpty()
    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = Spacing.large),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(Spacing.small))
        TopTexts(events = events, modifier = Modifier.padding(horizontal = 24.dp))
        when (contentState) {
            ContentState.Loading -> EventsLoadingState()

            is ContentState.Error -> StateMessage(
                icon = Icons.Default.EventBusy,
                title = stringResource(R.string.load_events_error_title),
                message = stringResource(R.string.load_events_error_message),
                actionLabel = stringResource(R.string.retry),
                onAction = onRetry,
                secondaryActionLabel = stringResource(R.string.explore_places),
                onSecondaryAction = onExplorePlacesClick
            )

            // The repository answered, and it has no events. That is an empty
            // schedule, not a connection failure.
            ContentState.Empty -> StateMessage(
                icon = Icons.Default.EventBusy,
                title = stringResource(R.string.no_events_title),
                message = stringResource(R.string.no_events_message),
                actionLabel = stringResource(R.string.explore_places),
                onAction = onExplorePlacesClick
            )

            is ContentState.Content -> {
                // Featured presentation is restricted to explicitly classified
                // festival content; a technology event never becomes "Featured".
                val featured = contentState.data.firstOrNull { it.isFeaturedEligible }
                if (featured != null) {
                    FeaturedEvent(
                        event = featured,
                        modifier = Modifier.padding(horizontal = 24.dp),
                        onEventClick = { onEventClick(featured.id) }
                    )
                }
                val upcoming = contentState.data.filterNot { it.id == featured?.id }
                if (upcoming.isNotEmpty()) {
                    UpcomingEvents(
                        events = upcoming,
                        modifier = Modifier.padding(horizontal = 24.dp).padding(top = 20.dp),
                        onEventClick = onEventClick
                    )
                }
            }
        }
    }
}

@Composable
private fun TopTexts(events: List<Event>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = stringResource(R.string.events),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = eventsScopeLabel(events.map { it.geographicScope }),
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Summarises the actual reach of the events on screen. This replaces the old
 * "All Over Nepal" label that was attached to the `kathmanduValley` key.
 */
@Composable
private fun eventsScopeLabel(scopes: List<GeographicScope>): String {
    val known = scopes.filterNot { it == GeographicScope.UNKNOWN }.toSet()
    return when {
        known.isEmpty() -> stringResource(R.string.scope_unknown)
        known.contains(GeographicScope.NATIONWIDE) &&
            known.any { it == GeographicScope.LOCAL || it == GeographicScope.REGIONAL } ->
            stringResource(R.string.events_scope_nationwide_local)
        known.contains(GeographicScope.NATIONWIDE) -> stringResource(R.string.scope_nationwide)
        known.contains(GeographicScope.LOCAL) && known.contains(GeographicScope.REGIONAL) ->
            stringResource(R.string.events_scope_local_regional)
        known.contains(GeographicScope.LOCAL) -> stringResource(R.string.scope_local)
        known.contains(GeographicScope.REGIONAL) -> stringResource(R.string.scope_regional)
        else -> stringResource(R.string.events_scope_across_nepal)
    }
}

@Composable
private fun FeaturedEvent(
    event: Event,
    modifier: Modifier = Modifier,
    onEventClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val cta = event.cta()
    val ctaRes = ctaLabelRes(cta)
    val actionLabel = ctaRes?.let { stringResource(it) } ?: stringResource(R.string.cta_view_details)
    val kindLabel = stringResource(eventKindLabelRes(event.primaryKind))
    val scopeLabel = stringResource(scopeLabelRes(event.geographicScope))
    val dateParts = event.date.split(" ", ",").filter { it.isNotBlank() }
    val month = dateParts.getOrNull(0)?.uppercase()
    val day = dateParts.getOrNull(1)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(
                if (event.primaryKind == EventKind.FESTIVAL) {
                    R.string.featured_festival
                } else {
                    R.string.featured_celebration
                }
            ),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Column {
                // Image container; AsyncImage fills it when the event has a URL.
                Box(
                    modifier = Modifier.fillMaxWidth().height(180.dp)
                        .background(SageSoft)
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (event.imageUrl != null) {
                        AsyncImage(
                            model = event.imageUrl,
                            contentDescription = event.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            Icons.Default.Landscape,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Surface(
                        modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                        shape = BadgeShape,
                        color = Terracotta
                    ) {
                        Text(
                            text = "COMING SOON",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextOnDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(PrimaryGreen, PrimaryGreenDark)
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = event.title.ifBlank { stringResource(R.string.untitled) },
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        if (month != null && day != null) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = month,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = day,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        } else {
                            Text(
                                text = stringResource(R.string.no_date),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                    Text(
                        text = event.description.ifBlank { stringResource(R.string.no_description) },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "$kindLabel · $scopeLabel",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(Modifier.height(6.dp))
                    Button(
                        onClick = {
                            if (cta != EventCta.NONE && event.hasValidJoinLink) {
                                if (!context.openUrlInBrowser(event.joinLink)) onEventClick()
                            } else {
                                onEventClick()
                            }
                        },
                        shape = BadgeShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.fillMaxWidth().height(40.dp)
                    ) {
                        Text(
                            text = actionLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UpcomingEvents(
    events: List<Event>,
    modifier: Modifier = Modifier,
    onEventClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.events_upcoming),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        events.forEach { event ->
            EventCards(
                event = event,
                onCardClick = { onEventClick(event.id) },
                onActionClick = {
                    if (event.hasValidJoinLink) context.openUrlInBrowser(event.joinLink)
                }
            )
        }
    }
}

@Composable
private fun EventCards(
    event: Event,
    onCardClick: () -> Unit = {},
    onActionClick: () -> Unit = {}
) {
    val price = event.priceDisplay()
    val actionRes = cardActionLabelRes(event.cta())
    Card(
        onClick = onCardClick,
        modifier = Modifier.fillMaxWidth(),
        shape = StandardCardShape,
        border = BorderStroke(1.dp, GreyBorder),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(shape = StandardCardShape, modifier = Modifier.size(56.dp)) {
                Box(
                    modifier = Modifier.fillMaxSize().background(SageSoft),
                    contentAlignment = Alignment.Center
                ) {
                    if (event.imageUrl != null) {
                        AsyncImage(
                            model = event.imageUrl,
                            contentDescription = event.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            Icons.Default.Landscape,
                            null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = event.title.ifBlank { stringResource(R.string.untitled) },
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = event.meta.ifBlank { stringResource(R.string.no_location) },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(eventKindLabelRes(event.primaryKind)) + " · " +
                        stringResource(scopeLabelRes(event.geographicScope)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (price != null) {
                    Text(
                        text = eventPriceValue(price.label, price.value),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
            if (actionRes != null && event.hasValidJoinLink) {
                Text(
                    text = stringResource(actionRes),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    modifier = Modifier.clickable(onClick = onActionClick)
                )
            }
        }
    }
}

@Preview
@Composable
private fun EventsPreview() {
    AvalokanTheme {
        EventContent(
            contentState = ContentState.Content(
                listOf(
                    Event("bhaktapur-pottery", "Bhaktapur Pottery Workshop", "", "Bhaktapur Square • 10:00 AM", "FREE ENTRY", "Join >"),
                    Event("samay-baji", "Alla & Samay Baji Festival", "", "Patan Square • 5:00 PM", "$15 ENTRY", "Join >"),
                    Event("thangka-demo", "Live Thangka Art Demo", "", "Boudha • 11:00 AM", "DONATION BASED", "Join >")
                )
            ),
            onEventClick = {},
            onRetry = {},
            onExplorePlacesClick = {}
        )
    }
}
