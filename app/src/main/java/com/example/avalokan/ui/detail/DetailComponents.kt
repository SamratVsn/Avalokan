/*
 * Copyright (C) 2026 Samrat Parajuli
 *
 * This file is part of Avalokan.
 *
 * Avalokan is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * Avalokan is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Avalokan. If not, see <https://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */

package com.example.avalokan.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.avalokan.R
import com.example.avalokan.ui.common.ContentState
import com.example.avalokan.ui.components.PhotoOverlayIconButton
import com.example.avalokan.ui.components.StateMessage
import com.example.avalokan.ui.theme.BadgeShape
import com.example.avalokan.ui.theme.Spacing

/**
 * Renders a detail screen's body across its four real states: content once the
 * item arrives, a loading indicator while it is still coming in, a not-found
 * screen when the repository answered with nothing, and a retryable error when
 * the load actually failed.
 */
@Composable
fun <T> DetailBody(
    state: ContentState<T>,
    kind: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
    content: @Composable (T) -> Unit
) {
    when (state) {
        ContentState.Loading -> DetailLoading(kind = kind, onBackClick = onBackClick, modifier = modifier)
        ContentState.Empty -> DetailMissing(kind = kind, onBackClick = onBackClick, modifier = modifier)
        is ContentState.Error -> DetailError(
            kind = kind,
            onBackClick = onBackClick,
            onRetry = onRetry,
            modifier = modifier
        )
        is ContentState.Content -> content(state.data)
    }
}

@Composable
fun BoxScope.DetailBackButton(onBackClick: () -> Unit) {
    PhotoOverlayIconButton(
        icon = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = stringResource(R.string.back),
        onClick = onBackClick,
        modifier = Modifier.align(Alignment.TopStart)
            .detailBackButtonPadding()
    )
}

/**
 * Keeps floating detail controls clear of the status bar and display cutout on
 * the top and start edges. Only those two sides are consumed so the control's
 * visual position does not change on the end or bottom edges.
 */
@Composable
internal fun Modifier.detailBackButtonPadding(): Modifier = this
    .windowInsetsPadding(
        WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Start)
    )
    .padding(top = 8.dp, start = 16.dp)

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
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.height(Spacing.medium))
            Text(
                text = stringResource(R.string.detail_loading, kind),
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
            title = stringResource(R.string.detail_not_found_title, kind),
            message = stringResource(R.string.detail_not_found_message),
            actionLabel = stringResource(R.string.detail_go_back),
            onAction = onBackClick,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun DetailError(
    kind: String,
    onBackClick: () -> Unit,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)
    ) {
        DetailBackButton(onBackClick = onBackClick)
        StateMessage(
            icon = Icons.Default.SearchOff,
            title = stringResource(R.string.detail_load_error_title, kind),
            message = stringResource(R.string.detail_load_error_message),
            actionLabel = if (onRetry != null) stringResource(R.string.detail_try_again) else null,
            onAction = onRetry,
            secondaryActionLabel = stringResource(R.string.detail_go_back),
            onSecondaryAction = onBackClick,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


@Composable
fun DetailBadge(
    text: String,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = BadgeShape,
        color = containerColor,
        contentColor = contentColor
    ) {
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
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
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
                color = if (index % 2 == 0) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
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
                        color = if (index % 2 == 0) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}
