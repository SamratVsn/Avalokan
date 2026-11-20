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

package com.example.avalokan.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.avalokan.R
import com.example.avalokan.domain.model.PlaceSort

/** The same sort options and labels on Discover and every category listing. */
@Composable
fun placeSortLabel(sort: PlaceSort): String = stringResource(
    when (sort) {
        PlaceSort.RECOMMENDED -> R.string.sort_recommended
        PlaceSort.NAME -> R.string.sort_name
        PlaceSort.REGION -> R.string.sort_region
        PlaceSort.UNESCO -> R.string.sort_unesco
    }
)

@Composable
fun PlaceSortControl(
    sort: PlaceSort,
    onSortChange: (PlaceSort) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        AssistChip(
            onClick = { expanded = true },
            label = {
                Text(
                    text = stringResource(R.string.sort_current, placeSortLabel(sort)),
                    style = androidx.compose.material3.MaterialTheme.typography.labelLarge
                )
            },
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(R.string.sort_by)
                )
            }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            PlaceSort.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(placeSortLabel(option)) },
                    onClick = {
                        onSortChange(option)
                        expanded = false
                    },
                    leadingIcon = if (option == sort) {
                        { Icon(Icons.Default.Check, contentDescription = null) }
                    } else {
                        null
                    }
                )
            }
        }
    }
}
