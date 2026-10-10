package com.example.avalokan.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.avalokan.ui.theme.AccentMarigold
import com.example.avalokan.ui.theme.AvalokanTheme
import com.example.avalokan.ui.theme.PrimaryTeal
import com.example.avalokan.ui.theme.Spacing

private data class StoryUi(
    val badge: String = "Cultural Insight",
    val title: String = "The Sacred Echoes of Boudhanath",
    val author: String = "Aravind Sharma",
    val date: String = "Sept 12, 2024",
    val readTime: String = "6 min read",
    val fact: String = "Boudhanath is one of the largest stupas in the world — its all-seeing Buddha eyes watch over Kathmandu from all four directions.",
    val body: List<String> = listOf(
        "\"Om Mani Padme Hum\" — the rhythmic chant flows through the air like the very breath of the stupa itself.",
        "Standing before the world's largest stupas, Boudhanath is not merely a monument. It is a living mandala: centuries of butter lamps, prayer wheels spun by pilgrims, and the watchful eyes of the Buddha painted on all four sides, gazing across over 1,500 years of devotion.",
        "As you walk clockwise around the stupa — the traditional kora — the sound of singing bowls, the scent of juniper incense, and the flutter of prayer flags carry centuries of stories into the soul of the city."
    )
)

@Composable
fun StoryDetailScreen(
    storyId: String = "preview",
    onBackClick: () -> Unit = {}
) {
    val story = StoryUi()
    Scaffold(
        containerColor = Color.White,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                // 350dp hero with back overlay (placeholder until Data layer image)
                Box(modifier = Modifier.fillMaxWidth().height(350.dp)) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
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
            item {
                // White sheet overlapping hero by -30dp, 40dp top rounding
                Surface(
                    modifier = Modifier.fillMaxWidth()
                        .offset(y = (-30).dp)
                        .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    StoryBody(
                        story = story,
                        modifier = Modifier.padding(horizontal = 24.dp)
                            .padding(top = 24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StoryBody(story: StoryUi, modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DetailBadge(text = story.badge)
        Text(
            text = story.title,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        // Metadata: avatar + author, date, read time
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier.size(32.dp)
                    .background(PrimaryTeal.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = story.author.first().toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = PrimaryTeal
                )
            }
            Text(
                text = story.author,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            Text(
                text = "•",
                style = MaterialTheme.typography.bodyMedium,
                color = PrimaryTeal
            )
            Text(
                text = story.date,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "•",
                style = MaterialTheme.typography.bodyMedium,
                color = PrimaryTeal
            )
            Text(
                text = story.readTime,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        story.body.forEach { paragraph ->
            Text(
                text = paragraph,
                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                color = Color(0xFF444444)
            )
        }
        // Interesting-fact callout, light teal
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = AccentMarigold,
                    modifier = Modifier.size(24.dp)
                )
                androidx.compose.foundation.layout.Column {
                    Text(
                        text = "Interesting Fact",
                        style = MaterialTheme.typography.titleMedium,
                        color = PrimaryTeal
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = story.fact,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        Spacer(Modifier.height(Spacing.small))
    }
}

@Preview
@Composable
private fun StoryDetailPreview() {
    AvalokanTheme {
        StoryDetailScreen()
    }
}
