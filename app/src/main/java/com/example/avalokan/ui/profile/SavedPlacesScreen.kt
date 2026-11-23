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

package com.example.avalokan.ui.profile

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ExploreOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.avalokan.R
import com.example.avalokan.domain.model.Place
import com.example.avalokan.ui.common.ContentState
import com.example.avalokan.ui.components.CircleIconButton
import com.example.avalokan.ui.components.DiscoverLoadingState
import com.example.avalokan.ui.components.PlaceComparisonCard
import com.example.avalokan.ui.components.StateMessage
import com.example.avalokan.ui.theme.AvalokanTheme
import com.example.avalokan.ui.theme.Spacing
import kotlinx.coroutines.launch

/**
 * Every saved place, reached from Profile's "See all". Uses the same
 * [PlaceComparisonCard] as Discover and the category listing; tapping the
 * bookmark removes a place, with an undo that restores the original order.
 */
@Composable
fun SavedPlacesScreen(
    onBackClick: () -> Unit = {},
    onPlaceClick: (String) -> Unit = {},
    onDiscoverClick: () -> Unit = {},
    viewModel: SavedPlacesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val removedMessage = stringResource(R.string.saved_place_removed)
    val undoLabel = stringResource(R.string.undo)

    SavedPlacesContent(
        state = state,
        snackbarHost = snackbarHost,
        onBackClick = onBackClick,
        onPlaceClick = onPlaceClick,
        onDiscoverClick = onDiscoverClick,
        onRetry = viewModel::retry,
        onRemove = { place ->
            val previous = viewModel.remove(place.id)
            scope.launch {
                snackbarHost.currentSnackbarData?.dismiss()
                val result = snackbarHost.showSnackbar(
                    message = removedMessage.format(place.name),
                    actionLabel = undoLabel,
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) viewModel.restore(previous)
            }
        }
    )
}

@Composable
private fun SavedPlacesContent(
    state: ContentState<List<Place>>,
    snackbarHost: SnackbarHostState,
    onBackClick: () -> Unit,
    onPlaceClick: (String) -> Unit,
    onDiscoverClick: () -> Unit,
    onRetry: () -> Unit,
    onRemove: (Place) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Spacer(Modifier.height(Spacing.extraSmall))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.sidePadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircleIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    onClick = onBackClick,
                    containerSize = 36.dp,
                    iconSize = 16.dp
                )
                Column {
                    Text(
                        text = stringResource(R.string.savedPlaces),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (state is ContentState.Content) {
                        Text(
                            text = pluralStringResource(
                                R.plurals.places_count,
                                state.data.size,
                                state.data.size
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            when (state) {
                ContentState.Loading -> DiscoverLoadingState()

                is ContentState.Error -> StateMessage(
                    icon = Icons.Default.ExploreOff,
                    title = stringResource(R.string.load_places_error_title),
                    message = stringResource(R.string.load_places_error_message),
                    actionLabel = stringResource(R.string.retry),
                    onAction = onRetry,
                    secondaryActionLabel = stringResource(R.string.discover_places),
                    onSecondaryAction = onDiscoverClick
                )

                ContentState.Empty -> StateMessage(
                    icon = Icons.Default.BookmarkBorder,
                    title = stringResource(R.string.saved_places_empty),
                    message = stringResource(R.string.saved_places_empty_hint),
                    actionLabel = stringResource(R.string.discover_places),
                    onAction = onDiscoverClick
                )

                is ContentState.Content -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 24.dp,
                        end = 24.dp,
                        top = 4.dp,
                        bottom = Spacing.large
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.data, key = { it.id }) { place ->
                        PlaceComparisonCard(
                            place = place,
                            isSaved = true,
                            onSaveClick = { onRemove(place) },
                            onClick = { onPlaceClick(place.id) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHost,
            modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding()
        )
    }
}

@Preview
@Composable
private fun SavedPlacesEmptyPreview() {
    AvalokanTheme {
        SavedPlacesContent(
            state = ContentState.Empty,
            snackbarHost = SnackbarHostState(),
            onBackClick = {},
            onPlaceClick = {},
            onDiscoverClick = {},
            onRetry = {},
            onRemove = {}
        )
    }
}
