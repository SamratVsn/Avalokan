package com.example.avalokan.ui.detail

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.avalokan.R
import com.example.avalokan.domain.model.Story
import com.example.avalokan.ui.components.PhotoOverlayIconButton
import com.example.avalokan.ui.theme.AvalokanTheme
import com.example.avalokan.ui.theme.Spacing

@Composable
fun StoryDetailScreen(
    storyId: String = "preview",
    onBackClick: () -> Unit = {},
    viewModel: StoryDetailViewModel = hiltViewModel()
) {
    val storyState by viewModel.storyState.collectAsState()
    DetailBody(
        state = storyState,
        kind = "story",
        onBackClick = onBackClick,
        onRetry = viewModel::retry
    ) { story ->
        StoryDetailContent(story = story, onBackClick = onBackClick)
    }
}

@Composable
private fun StoryDetailContent(
    story: Story,
    onBackClick: () -> Unit
) {
    val blocks = remember(story.description) { parseStoryBody(story.description) }
    val fallbackTitle = stringResource(R.string.untitled)
    val shareTitle = story.title.ifBlank { fallbackTitle }
    val shareText = buildStoryShareText(
        title = story.title,
        author = story.author,
        readTime = story.readTime,
        fallbackTitle = fallbackTitle,
    )
    val shareChooserTitle = stringResource(R.string.share_story)
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var isLiked by rememberSaveable { mutableStateOf(false) }
    var isSaved by rememberSaveable { mutableStateOf(false) }
    val onShareClick = {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        context.startActivity(Intent.createChooser(send, shareChooserTitle))
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
                            val heroImage = story.imageUrl
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
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
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
                            blocks = blocks,
                            modifier = Modifier.padding(horizontal = 24.dp)
                                .padding(top = 24.dp)
                        )
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
                tint = if (isLiked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
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
                tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
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
private fun StoryBody(
    story: Story,
    blocks: List<StoryBlock>,
    modifier: Modifier = Modifier,
) {
    val metadata = StoryMetadata.from(story)
    Column(
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
        // Only render details the story actually contains. A blank field means
        // "not recorded", so the row disappears instead of saying "not available".
        metadata.author?.let { StoryAuthor(it) }
        if (metadata.hasDateOrReadTime) {
            StoryDateAndReadTime(date = metadata.date, readTime = metadata.readTime)
        }
        metadata.curator?.let { curator ->
            Text(
                text = stringResource(R.string.story_curated_by, curator),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (blocks.isEmpty()) {
            Text(
                text = stringResource(R.string.no_story_content),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
            )
        } else {
            StoryBlocks(blocks)
        }
        if (metadata.hasProvenance) {
            StoryProvenance(metadata)
        }
        Spacer(Modifier.height(Spacing.small))
    }
}

@Composable
private fun StoryAuthor(author: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier.size(32.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = author.firstOrNull()?.uppercase() ?: "",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = author,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun StoryDateAndReadTime(date: String?, readTime: String?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        date?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (date != null && readTime != null) {
            Text(
                text = "•",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        readTime?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StoryBlocks(blocks: List<StoryBlock>) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        blocks.forEach { block ->
            when (block) {
                is StoryBlock.Heading -> StoryHeading(block)
                is StoryBlock.Paragraph -> StoryParagraph(block.spans)
                is StoryBlock.BulletList -> StoryList(block.items, ordered = false)
                is StoryBlock.NumberedList -> StoryList(block.items, ordered = true)
                is StoryBlock.Quote -> StoryQuote(block.spans)
                StoryBlock.Divider -> HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Composable
private fun StoryHeading(block: StoryBlock.Heading) {
    val style = when (block.level) {
        1 -> MaterialTheme.typography.headlineMedium
        2 -> MaterialTheme.typography.headlineSmall
        3 -> MaterialTheme.typography.titleLarge
        else -> MaterialTheme.typography.titleMedium
    }
    Text(
        text = block.spans.toAnnotatedString(MaterialTheme.colorScheme.primary),
        style = style,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun StoryParagraph(spans: List<StorySpan>) {
    Text(
        text = spans.toAnnotatedString(MaterialTheme.colorScheme.primary),
        style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 26.sp),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
    )
}

@Composable
private fun StoryList(items: List<List<StorySpan>>, ordered: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEachIndexed { index, item ->
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (ordered) "${index + 1}." else "•",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = item.toAnnotatedString(MaterialTheme.colorScheme.primary),
                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StoryQuote(spans: List<StorySpan>) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
        )
        Text(
            text = spans.toAnnotatedString(MaterialTheme.colorScheme.primary),
            style = MaterialTheme.typography.bodyLarge.copy(
                lineHeight = 24.sp,
                fontStyle = FontStyle.Italic
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        )
    }
}

@Composable
private fun StoryProvenance(metadata: StoryMetadata) {
    val sourceLabel = metadata.sourceLabel ?: metadata.sourceUrl
    val safeUrl = metadata.sourceUrl?.takeIf(::isSafeStoryLink)
    val sourcePrefix = stringResource(R.string.story_source) + ": "
    val linkStyle = SpanStyle(
        color = MaterialTheme.colorScheme.primary,
        textDecoration = TextDecoration.Underline
    )
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        if (sourceLabel != null) {
            val source = buildAnnotatedString {
                append(sourcePrefix)
                if (safeUrl != null) {
                    withLink(
                        LinkAnnotation.Url(
                            url = safeUrl,
                            styles = TextLinkStyles(style = linkStyle)
                        )
                    ) {
                        append(sourceLabel)
                    }
                } else {
                    append(sourceLabel)
                }
            }
            Text(
                text = source,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        metadata.lastVerifiedAt?.let { verified ->
            Text(
                text = stringResource(R.string.story_last_verified, verified),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun List<StorySpan>.toAnnotatedString(linkColor: Color): AnnotatedString =
    buildAnnotatedString {
        this@toAnnotatedString.forEach { span ->
            val baseStyle = SpanStyle(
                fontWeight = if (span.bold) FontWeight.SemiBold else null,
                fontStyle = if (span.italic) FontStyle.Italic else null
            )
            val url = span.link
            if (url != null) {
                withLink(
                    LinkAnnotation.Url(
                        url = url,
                        styles = TextLinkStyles(
                            style = baseStyle.copy(
                                color = linkColor,
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    )
                ) {
                    append(span.text)
                }
            } else {
                withStyle(baseStyle) {
                    append(span.text)
                }
            }
        }
    }

@Preview(name = "Story Detail — Light")
@Preview(
    name = "Story Detail — Dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun StoryDetailPreview() {
    AvalokanTheme {
        StoryBody(
            story = Story(
                id = "preview",
                title = "The Sacred Echoes of Boudhanath",
                description = "Preview",
                badge = "Cultural Insight",
                author = "Samrat Parajuli",
                date = "Sept 12, 2024",
                readTime = "6 min read",
                curator = "Avalokan heritage desk",
                sourceLabel = "Department of Archaeology",
                sourceUrl = "https://example.com/source",
                lastVerifiedAt = "2026-10-01"
            ),
            blocks = parseStoryBody(
                """
                ## A living mandala

                Prayer wheels and butter lamps fill the square.

                - Walk clockwise
                - **Remove shoes** before entering

                > The stupa is a mandala of the mind.

                [Read the source](https://example.com/source)
                """.trimIndent()
            ),
            modifier = Modifier.padding(24.dp)
        )
    }
}
