package com.example.avalokan.ui.events

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Landscape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.avalokan.R
import com.example.avalokan.data.event.EventItem
import com.example.avalokan.ui.theme.AccentMarigold
import com.example.avalokan.ui.theme.AvalokanTheme
import com.example.avalokan.ui.theme.BadgeShape
import com.example.avalokan.ui.theme.PrimaryLight
import com.example.avalokan.ui.theme.PrimaryTeal
import com.example.avalokan.ui.theme.Spacing
import com.example.avalokan.ui.theme.StandardCardShape

@Composable
fun EventScreen(
    onEventClick: (String) -> Unit = {},
    viewModel: EventsViewModel = hiltViewModel()
) {
    val events by viewModel.events.collectAsState()

    EventContent(
        events = events,
        onEventClick = onEventClick
    )
}

@Composable
private fun EventContent(
    events: List<EventItem>,
    onEventClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = Spacing.large),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(Spacing.small))
        TopTexts(modifier = Modifier.padding(horizontal = Spacing.sidePadding))
        FeaturedEvent(
            modifier = Modifier.padding(horizontal = Spacing.sidePadding),
            onRemindClick = { onEventClick("indra-jatra-2024") }
        )
        UpcomingEvents(
            events = events,
            modifier = Modifier.padding(horizontal = Spacing.sidePadding),
            onEventClick = onEventClick
        )
    }
}

@Composable
private fun TopTexts(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = stringResource(R.string.events),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.kathmanduValley),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(
            onClick = { /* TODO: calendar */ },
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun FeaturedEvent(
    modifier: Modifier = Modifier,
    // API-ready: pass remote image URL/model here later; null = placeholder below
    imageUrl: String? = null,
    onRemindClick: () -> Unit = {}
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Featured Festival",
            style = MaterialTheme.typography.titleMedium
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = StandardCardShape,
            colors = CardDefaults.cardColors(containerColor = PrimaryTeal)
        ) {
            Column {
                // Image container (reference aspect); swap for AsyncImage when Data layer lands
                Box(
                    modifier = Modifier.fillMaxWidth().height(180.dp)
                        .background(PrimaryLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Landscape,
                        contentDescription = null,
                        tint = PrimaryTeal,
                        modifier = Modifier.size(32.dp)
                    )
                    Surface(
                        modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                        shape = BadgeShape,
                        color = AccentMarigold
                    ) {
                        Text(
                            text = "COMING SOON",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "Indra Jatra 2024",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "SEPT",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "17",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    Text(
                        text = "The biggest religious street festival in Kathmandu, celebrating the end of monsoon with masked dances and chariot processions.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(6.dp))
                    Button(
                        onClick = onRemindClick,
                        shape = BadgeShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = PrimaryTeal
                        ),
                        modifier = Modifier.fillMaxWidth().height(40.dp)
                    ) {
                        Text(
                            text = "Remind Me & Get Tickets",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UpcomingEvents(
    events: List<EventItem>,
    modifier: Modifier = Modifier,
    onEventClick: (String) -> Unit = {}
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Upcoming in Your Locality",
            style = MaterialTheme.typography.titleMedium
        )
        events.forEach { event ->
            EventCards(
                name = event.title,
                meta = event.meta,
                fee = event.fee,
                action = event.action,
                onCardClick = { onEventClick(event.id) }
            )
        }
    }
}

@Composable
private fun EventCards(
    name: String,
    meta: String,
    fee: String,
    action: String,
    // API-ready: pass remote image URL/model here later; null = placeholder below
    imageUrl: String? = null,
    onCardClick: () -> Unit = {}
) {
    Card(
        onClick = onCardClick,
        modifier = Modifier.fillMaxWidth(),
        shape = StandardCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(shape = StandardCardShape, modifier = Modifier.size(56.dp)) {
                Box(
                    modifier = Modifier.fillMaxSize().background(PrimaryLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Landscape,
                        null,
                        tint = PrimaryTeal,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = meta,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = fee,
                    style = MaterialTheme.typography.labelSmall,
                    color = AccentMarigold
                )
            }
            Text(
                text = action,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )
        }
    }
}

@Preview
@Composable
private fun EventsPreview() {
    AvalokanTheme {
        EventContent(
            events = listOf(
                EventItem("bhaktapur-pottery", "Bhaktapur Pottery Workshop", "", "Bhaktapur Square • 10:00 AM", "FREE ENTRY", "Join >"),
                EventItem("samay-baji", "Alla & Samay Baji Festival", "", "Patan Square • 5:00 PM", "$15 ENTRY", "Sign Up >"),
                EventItem("thangka-demo", "Live Thangka Art Demo", "", "Boudha • 11:00 AM", "DONATION BASED", "More Info >")
            ),
            onEventClick = {}
        )
    }
}
