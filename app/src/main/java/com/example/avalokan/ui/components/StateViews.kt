package com.example.avalokan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.avalokan.ui.theme.DangerBg
import com.example.avalokan.ui.theme.DangerRed
import com.example.avalokan.ui.theme.PrimaryGreen
import com.example.avalokan.ui.theme.SageSoft
import com.example.avalokan.ui.theme.Spacing

/**
 * Friendly, centred placeholder for empty / error / not-found screens.
 *
 * Besides the primary action, it can offer a secondary escape hatch and a row
 * of suggestions, so an empty screen always has somewhere to go instead of
 * being a dead end.
 */
@Composable
fun StateMessage(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    iconTint: Color = PrimaryGreen,
    iconBackground: Color = SageSoft,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    secondaryActionLabel: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    suggestions: List<String> = emptyList(),
    suggestionsLabel: String? = null,
    onSuggestionClick: ((String) -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(72.dp).background(iconBackground, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(30.dp)
            )
        }
        Spacer(Modifier.height(Spacing.medium))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Spacing.extraSmall))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(Spacing.large))
            Button(
                onClick = onAction,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(text = actionLabel, fontWeight = FontWeight.SemiBold)
            }
        }
        if (secondaryActionLabel != null && onSecondaryAction != null) {
            TextButton(onClick = onSecondaryAction) {
                Text(text = secondaryActionLabel, fontWeight = FontWeight.Medium)
            }
        }
        if (suggestions.isNotEmpty() && onSuggestionClick != null) {
            Spacer(Modifier.height(Spacing.small))
            if (suggestionsLabel != null) {
                Text(
                    text = suggestionsLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(Spacing.extraSmall))
            }
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    8.dp,
                    Alignment.CenterHorizontally
                ),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(suggestions) { suggestion ->
                    AssistChip(
                        onClick = { onSuggestionClick(suggestion) },
                        label = { Text(suggestion) }
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
    onRetryLabel: String = "Try again"
) {
    StateMessage(
        icon = icon,
        title = title,
        message = message,
        modifier = modifier,
        iconTint = DangerRed,
        iconBackground = DangerBg,
        actionLabel = if (onRetry != null) onRetryLabel else null,
        onAction = onRetry
    )
}
