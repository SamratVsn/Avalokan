package com.example.avalokan.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.avalokan.ui.theme.Spacing

/**
 * One infinite transition shared by every placeholder in a skeleton screen, so
 * the whole layout pulses together instead of each block animating separately.
 */
@Composable
fun SkeletonHost(content: @Composable () -> Unit) {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val alpha by transition.animateFloat(
        initialValue = 0.28f,
        targetValue = 0.72f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "skeleton-alpha"
    )
    CompositionLocalProvider(LocalSkeletonAlpha provides alpha, content = content)
}

private val LocalSkeletonAlpha = staticCompositionLocalOf { 0.45f }

@Composable
fun SkeletonBlock(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp)
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant
                    .copy(alpha = LocalSkeletonAlpha.current),
                shape = shape
            )
    )
}

/** Matches the tall discovery card: image, title, meta, then body lines. */
@Composable
fun PlaceCardSkeleton(
    modifier: Modifier = Modifier,
    imageHeight: Dp = 170.dp
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SkeletonBlock(
            modifier = Modifier.fillMaxWidth().height(imageHeight),
            shape = RoundedCornerShape(28.dp)
        )
        SkeletonBlock(Modifier.fillMaxWidth(0.65f).height(16.dp))
        SkeletonBlock(Modifier.fillMaxWidth(0.4f).height(11.dp))
        SkeletonBlock(Modifier.fillMaxWidth().height(11.dp))
        SkeletonBlock(Modifier.fillMaxWidth(0.82f).height(11.dp))
    }
}

/** Matches the compact horizontal cards used on Home. */
@Composable
fun SmallPlaceCardSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.width(150.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SkeletonBlock(
            modifier = Modifier.fillMaxWidth().height(110.dp),
            shape = RoundedCornerShape(24.dp)
        )
        SkeletonBlock(Modifier.fillMaxWidth(0.9f).height(14.dp))
        SkeletonBlock(Modifier.fillMaxWidth(0.6f).height(10.dp))
    }
}

/** Matches one upcoming-event row: thumbnail, title, place. */
@Composable
fun EventRowSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SkeletonBlock(
            modifier = Modifier.size(56.dp),
            shape = RoundedCornerShape(16.dp)
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            SkeletonBlock(Modifier.fillMaxWidth(0.7f).height(14.dp))
            SkeletonBlock(Modifier.fillMaxWidth(0.45f).height(10.dp))
        }
    }
}

/** Skeleton for the Discover list, replacing the old "couldn't load" flash. */
@Composable
fun DiscoverLoadingState(modifier: Modifier = Modifier) {
    SkeletonHost {
        Column(
            modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            repeat(3) {
                PlaceCardSkeleton(imageHeight = 160.dp)
            }
        }
    }
}

/** Skeleton for the Home "Historical Gems" rail. */
@Composable
fun HomeLoadingState(modifier: Modifier = Modifier) {
    SkeletonHost {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.small)
        ) {
            SkeletonBlock(
                modifier = Modifier.padding(horizontal = 24.dp)
                    .fillMaxWidth(0.42f)
                    .height(18.dp)
            )
            Row(
                modifier = Modifier.padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                repeat(2) {
                    SmallPlaceCardSkeleton()
                }
            }
        }
    }
}

/** Skeleton for the Events screen: featured card plus upcoming rows. */
@Composable
fun EventsLoadingState(modifier: Modifier = Modifier) {
    SkeletonHost {
        Column(
            modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            SkeletonBlock(
                modifier = Modifier.fillMaxWidth().height(170.dp),
                shape = RoundedCornerShape(32.dp)
            )
            repeat(3) {
                EventRowSkeleton()
            }
        }
    }
}
