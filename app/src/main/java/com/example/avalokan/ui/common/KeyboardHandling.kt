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

package com.example.avalokan.ui.common

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

/**
 * Returns an action that hides the IME and drops text focus. Call it before
 * navigating away from a screen with a text field so the keyboard never
 * survives onto the next destination.
 */
@Composable
fun rememberKeyboardDismisser(): () -> Unit {
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    return remember(focusManager, keyboard) {
        {
            keyboard?.hide()
            focusManager.clearFocus(force = true)
        }
    }
}

/**
 * Keyboard hygiene for any screen that hosts text input:
 *  - back closes the IME first and only navigates on the next press;
 *  - when the IME is closed by any route (system back, swipe, IME button), the
 *    field loses focus too, so a stale focused field cannot reopen it.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KeyboardBackHandling() {
    val dismiss = rememberKeyboardDismisser()
    val imeVisible = WindowInsets.isImeVisible
    BackHandler(enabled = imeVisible, onBack = dismiss)
    LaunchedEffect(imeVisible) {
        if (!imeVisible) dismiss()
    }
}

/**
 * Taps that land on empty space (not consumed by a button, card or field)
 * dismiss the keyboard.
 */
fun Modifier.dismissKeyboardOnTap(dismiss: () -> Unit): Modifier =
    pointerInput(dismiss) { detectTapGestures(onTap = { dismiss() }) }

/**
 * A user drag on the scrollable content dismisses the keyboard. Only
 * [NestedScrollSource.UserInput] counts, so a programmatic bring-into-view
 * scroll triggered by focusing the field does not immediately close it again.
 */
@Composable
fun Modifier.dismissKeyboardOnScroll(dismiss: () -> Unit): Modifier {
    val connection = remember(dismiss) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source == NestedScrollSource.UserInput && available.y != 0f) dismiss()
                return Offset.Zero
            }
        }
    }
    return nestedScroll(connection)
}
