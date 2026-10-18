package com.example.avalokan.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.avalokan.ui.components.StateMessage
import com.example.avalokan.ui.theme.BadgeShape
import com.example.avalokan.ui.theme.HeroScrim
import com.example.avalokan.ui.theme.PrimaryLight
import com.example.avalokan.ui.theme.PrimaryTeal
import com.example.avalokan.ui.theme.Spacing
import kotlinx.coroutines.delay

/**
 * True once [itemPresent] has stayed false long enough that we're no longer
 * waiting on an expected load. Lets a detail screen show a brief loading
 * indicator before it commits to a friendly "couldn't load" message.
 */
@Composable
fun isStillMissing(itemPresent: Boolean, timeoutMillis: Long = 4_000): Boolean {
    var timedOut by remember(itemPresent) { mutableStateOf(false) }
    LaunchedEffect(itemPresent) {
        if (itemPresent) {
            timedOut = false
        } else {
            timedOut = false
            delay(timeoutMillis)
            timedOut = true
        }
    }
    return !itemPresent && timedOut
}

/**
 * Renders a detail screen's body across its three real states: content once the
 * item arrives, a loading indicator while it's still coming in, and a friendly
 * error screen if it never does.
 */
@Composable
fun DetailBody(
    itemPresent: Boolean,
    kind: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    when {
        itemPresent -> content()
        isStillMissing(itemPresent) -> DetailMissing(kind = kind, onBackClick = onBackClick, modifier = modifier)
        else -> DetailLoading(kind = kind, onBackClick = onBackClick, modifier = modifier)
    }
}

@Composable
fun BoxScope.DetailBackButton(onBackClick: () -> Unit) {
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

@Composable
fun DetailLoading(kind: String, onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)
    ) {
        DetailBackButton(onBackClick = onBackClick)
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = PrimaryTeal, modifier = Modifier.size(40.dp))
            Spacer(Modifier.height(Spacing.medium))
            Text(
                text = "Loading $kind…",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DetailMissing(kind: String, onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)
    ) {
        DetailBackButton(onBackClick = onBackClick)
        StateMessage(
            icon = Icons.Default.SearchOff,
            title = "We couldn’t load this $kind",
            message = "It may have been removed, or your connection is offline. Go back and try again.",
            actionLabel = "Go back",
            onAction = onBackClick,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


@Composable
fun DetailBadge(
    text: String,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = PrimaryTeal,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier, shape = BadgeShape, color = containerColor) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (icon != null) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(12.dp)
                )
            }
            Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = contentColor
            )
        }
    }
}

@Composable
fun MetaRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(36.dp)
                .background(
                    color = PrimaryLight,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = PrimaryTeal,
                modifier = Modifier.size(18.dp)
            )
        }
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun DetailSectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
fun Facepile(initials: List<String>, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        initials.forEachIndexed { index, initial ->
            Surface(
                shape = CircleShape,
                color = if (index % 2 == 0) PrimaryTeal else MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = 2.dp,
                modifier = Modifier.offset(x = if (index == 0) 0.dp else (-8).dp)
            ) {
                Box(
                    modifier = Modifier.size(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initial,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (index % 2 == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
