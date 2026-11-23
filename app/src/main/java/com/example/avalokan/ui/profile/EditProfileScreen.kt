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

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.avalokan.R
import com.example.avalokan.ui.common.KeyboardBackHandling
import com.example.avalokan.ui.common.dismissKeyboardOnTap
import com.example.avalokan.ui.common.rememberKeyboardDismisser
import com.example.avalokan.ui.components.CircleIconButton
import com.example.avalokan.ui.components.HeritageAvatar
import com.example.avalokan.ui.theme.AvalokanTheme
import com.example.avalokan.ui.theme.Spacing
import com.example.avalokan.ui.theme.StandardButtonShape

@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit = {},
    onSaved: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val userName by viewModel.userName.collectAsState()
    val bio by viewModel.bio.collectAsState()
    val avatarUri by viewModel.avatarUri.collectAsState()
    var nameDraft by rememberSaveable(userName) { mutableStateOf(userName) }
    var bioDraft by rememberSaveable(bio) { mutableStateOf(bio) }
    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.toString()?.let(viewModel::setAvatar) }

    EditProfileContent(
        userName = nameDraft,
        onNameChange = { nameDraft = it },
        bioDraft = bioDraft,
        onBioChange = { bioDraft = it },
        avatarUri = avatarUri,
        onAvatarClick = {
            photoPicker.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onSaveClick = {
            viewModel.updateName(nameDraft)
            viewModel.updateBio(bioDraft)
            onSaved()
        },
        onBackClick = onBackClick
    )
}

@Composable
private fun EditProfileContent(
    userName: String,
    onNameChange: (String) -> Unit,
    bioDraft: String,
    onBioChange: (String) -> Unit,
    avatarUri: String?,
    onAvatarClick: () -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val dismissKeyboard = rememberKeyboardDismisser()
    KeyboardBackHandling()
    // Form with a Save action under the fields: imePadding + scroll keeps it
    // reachable above the keyboard.
    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .dismissKeyboardOnTap(dismissKeyboard)
            .padding(bottom = Spacing.extraLarge),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(Modifier.height(Spacing.small))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.sidePadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CircleIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
                onClick = {
                    dismissKeyboard()
                    onBackClick()
                },
                containerSize = 36.dp,
                iconSize = 16.dp
            )
            Text(
                text = stringResource(R.string.editProfile),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.sidePadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                HeritageAvatar(
                    imageUrl = avatarUri,
                    fallbackText = userName.firstOrNull()?.uppercase() ?: "G",
                    contentDescription = stringResource(R.string.profile_photo),
                    modifier = Modifier.size(96.dp)
                )
                androidx.compose.material3.Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.clickable(onClick = onAvatarClick)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = stringResource(R.string.change_profile_photo),
                        modifier = Modifier.padding(6.dp).size(14.dp)
                    )
                }
            }
            Text(
                text = "Tap the badge to change your photo",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        OutlinedTextField(
            value = userName,
            onValueChange = onNameChange,
            label = { Text(text = "Display name") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.sidePadding)
        )
        OutlinedTextField(
            value = bioDraft,
            onValueChange = onBioChange,
            label = { Text(text = "Bio") },
            minLines = 3,
            maxLines = 5,
            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.sidePadding)
        )
        Button(
            onClick = {
                dismissKeyboard()
                onSaveClick()
            },
            shape = StandardButtonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = Spacing.sidePadding)
                .height(48.dp)
        ) {
            Text(text = "Save")
        }
    }
}

@Preview(name = "Edit Profile — Light")
@Preview(
    name = "Edit Profile — Dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun EditProfilePreview() {
    AvalokanTheme {
        EditProfileContent(
            userName = "Guest",
            onNameChange = {},
            bioDraft = "Exploring the heritage of Nepal",
            onBioChange = {},
            avatarUri = null,
            onAvatarClick = {},
            onSaveClick = {},
            onBackClick = {}
        )
    }
}
