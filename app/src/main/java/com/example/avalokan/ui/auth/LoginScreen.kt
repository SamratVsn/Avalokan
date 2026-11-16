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

package com.example.avalokan.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.avalokan.R
import com.example.avalokan.ui.common.KeyboardBackHandling
import com.example.avalokan.ui.common.dismissKeyboardOnTap
import com.example.avalokan.ui.common.rememberKeyboardDismisser
import com.example.avalokan.ui.theme.AvalokanTheme
import com.example.avalokan.ui.theme.Spacing
import com.example.avalokan.ui.theme.StandardButtonShape
import com.example.avalokan.ui.theme.StandardCardShape

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isSignUpMode by viewModel.isSignUpMode.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LoginContent(
        email = email,
        password = password,
        isSignUpMode = isSignUpMode,
        isLoading = isLoading,
        canSubmitEmail = viewModel.canSubmitEmailAuth,
        error = error,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onToggleMode = viewModel::onToggleMode,
        onEmailSubmit = { viewModel.onEmailSubmit(onLoggedIn) },
        onGoogleSubmit = { viewModel.onGoogleSubmit(it, onLoggedIn) }
    )
}

@Composable
private fun LoginContent(
    email: String,
    password: String,
    isSignUpMode: Boolean,
    isLoading: Boolean,
    canSubmitEmail: Boolean,
    error: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onToggleMode: () -> Unit,
    onEmailSubmit: () -> Unit,
    onGoogleSubmit: (String) -> Unit
) {
    val serverClientId = stringResource(R.string.google_server_client_id)
    val focusManager = LocalFocusManager.current
    val dismissKeyboard = rememberKeyboardDismisser()
    KeyboardBackHandling()
    // A form with actions below the fields: imePadding + scroll keeps the
    // submit button reachable above the keyboard on small screens.
    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .dismissKeyboardOnTap(dismissKeyboard)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))
        Icon(
            painter = painterResource(R.drawable.avalokansplash),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier.size(72.dp),
            tint = androidx.compose.ui.graphics.Color.Unspecified
        )
        Text(
            text = stringResource(
                if (isSignUpMode) R.string.joinHeritage else R.string.welcomeBack
            ),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text(text = stringResource(R.string.email)) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            singleLine = true,
            enabled = !isLoading,
            shape = StandardCardShape,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text(text = stringResource(R.string.password)) },
            supportingText = {
                Text(
                    text = stringResource(
                        if (isSignUpMode) R.string.password_hint_sign_up
                        else R.string.password_hint_sign_in
                    )
                )
            },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    dismissKeyboard()
                    if (!isLoading && canSubmitEmail) onEmailSubmit()
                }
            ),
            singleLine = true,
            enabled = !isLoading,
            shape = StandardCardShape,
            modifier = Modifier.fillMaxWidth()
        )
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
        Button(
            onClick = {
                dismissKeyboard()
                onEmailSubmit()
            },
            enabled = !isLoading && canSubmitEmail,
            shape = StandardButtonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = stringResource(
                        if (isSignUpMode) R.string.signUp else R.string.logIn
                    ),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        OutlinedButton(
            onClick = {
                dismissKeyboard()
                onGoogleSubmit(serverClientId)
            },
            enabled = !isLoading,
            shape = StandardButtonShape,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Icon(Icons.Default.Email, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text(text = stringResource(R.string.continueWithGoogle))
        }
        TextButton(onClick = onToggleMode, enabled = !isLoading) {
            Text(
                text = stringResource(
                    if (isSignUpMode) R.string.haveAccount else R.string.noAccount
                ),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview
@Composable
private fun LoginPreview() {
    AvalokanTheme {
        LoginContent(
            email = "",
            password = "",
            isSignUpMode = false,
            isLoading = false,
            canSubmitEmail = false,
            error = null,
            onEmailChange = {},
            onPasswordChange = {},
            onToggleMode = {},
            onEmailSubmit = {},
            onGoogleSubmit = {}
        )
    }
}
