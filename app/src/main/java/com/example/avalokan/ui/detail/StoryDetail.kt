package com.example.avalokan.ui.detail

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.avalokan.R
import com.example.avalokan.ui.theme.AvalokanTheme
import com.example.avalokan.ui.theme.DangerRed
import com.example.avalokan.ui.theme.PrimaryTeal
import com.example.avalokan.ui.theme.Spacing

private data class StoryUi(
    val badge: String,
    val title: String,
    val author: String,
    val date: String,
    val readTime: String,
    val body: List<String>
)

@Composable
fun StoryDetailScreen(
    storyId: String = "preview",
    onBackClick: () -> Unit = {},
    viewModel: StoryDetailViewModel = hiltViewModel()
) {
    val storyItem = viewModel.story.collectAsState().value
    if (storyItem == null) {
        if (isStillMissing(itemPresent = false)) {
            DetailMissing(kind = "story", onBackClick = onBackClick)
        } else {
            DetailLoading(kind = "story", onBackClick = onBackClick)
        }
        return
    }
    val story = StoryUi(
        badge = storyItem.badge,
        title = storyItem.title,
        author = storyItem.author,
        date = storyItem.date,
        readTime = storyItem.readTime,
        body = storyItem.description
            .split("\n\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    )
    val shareTitle = story.title.ifBlank { stringResource(R.string.untitled) }
    val shareAuthor = story.author.ifBlank { stringResource(R.string.no_author) }
    val shareReadTime = story.readTime.ifBlank { stringResource(R.string.no_read_time) }
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var isLiked by rememberSaveable { mutableStateOf(false) }
    var isSaved by rememberSaveable { mutableStateOf(false) }
    val onShareClick = {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                "“$shareTitle” — a story by $shareAuthor ($shareReadTime) on Avalokan, a Nepal Heritage Explorer."
            )
        }
        context.startActivity(Intent.createChooser(send, "Share story"))
    }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(hostState = snackbarHost) },
        bottomBar = {
            StoryBottomBar(
                isLiked = isLiked,
                isSaved = isSaved,
                onLike = {
                    isLiked = !isLiked
                    if (isLiked) scope.launch { snackbarHost.showSnackbar("Thanks for the love!") }
                },
                onShare = onShareClick,
                onSave = {
                    isSaved = !isSaved
                    scope.launch {
                        snackbarHost.showSnackbar(
                            if (isSaved) "Saved to Collection" else "Removed from Collection"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    // 350dp hero with back overlay (AsyncImage when the story has one)
                    Box(modifier = Modifier.fillMaxWidth().height(350.dp)) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            val heroImage = storyItem.imageUrl
                            if (heroImage != null) {
                                coil.compose.AsyncImage(
                                    model = heroImage,
                                    contentDescription = shareTitle,
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = PrimaryTeal,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
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
private fun StoryBottomBar(
    isLiked: Boolean,
    isSaved: Boolean,
    onLike: () -> Unit,
    onShare: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = Spacing.medium, vertical = Spacing.small)
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StoryBarAction(
                icon = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                label = if (isLiked) "Liked" else "Like",
                tint = if (isLiked) DangerRed else MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = onLike
            )
            StoryBarAction(
                icon = Icons.Default.Share,
                label = "Share",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = onShare
            )
            StoryBarAction(
                icon = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                label = if (isSaved) "Saved" else "Save",
                tint = if (isSaved) PrimaryTeal else MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = onSave
            )
        }
    }
}

@Composable
private fun StoryBarAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clip(RoundedCornerShape(16.dp))
    ) {
        IconButton(onClick = onClick) {
            Box(
                modifier = Modifier.size(44.dp)
                    .background(tint.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = tint,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = tint
        )
    }
}

@Composable
private fun StoryBody(story: StoryUi, modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (story.badge.isNotBlank()) {
            DetailBadge(text = story.badge)
        }
        Text(
            text = story.title.ifBlank { stringResource(R.string.untitled) },
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        // Metadata: avatar + author on one row, date and read time below.
        val authorLabel = story.author.ifBlank { stringResource(R.string.no_author) }
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
                    text = authorLabel.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleMedium,
                    color = PrimaryTeal
                )
            }
            Text(
                text = authorLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = story.date.ifBlank { stringResource(R.string.no_date) },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "•",
                style = MaterialTheme.typography.bodyMedium,
                color = PrimaryTeal
            )
            Text(
                text = story.readTime.ifBlank { stringResource(R.string.no_read_time) },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (story.body.isEmpty()) {
            Text(
                text = stringResource(R.string.no_story_content),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
            )
        } else {
            story.body.forEach { paragraph ->
                Text(
                    text = paragraph,
                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                )
            }
        }
        Spacer(Modifier.height(Spacing.small))
    }
}

@Preview
@Composable
private fun StoryDetailPreview() {
    AvalokanTheme {
        StoryBody(
            story = StoryUi(
                badge = "Cultural Insight",
                title = "The Sacred Echoes of Boudhanath",
                author = "Samrat Parajuli",
                date = "Sept 12, 2024",
                readTime = "6 min read",
                body = listOf("Preview content for the story detail screen.")
            )
        )
    }
}
