package com.example.avalokan.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.avalokan.R

@Composable
fun AuthPromptDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!visible) return
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.auth_required_title)) },
        text = { Text(stringResource(R.string.auth_required_message)) },
        confirmButton = {
            TextButton(onClick = onSignIn) {
                Text(stringResource(R.string.auth_sign_in_up))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.auth_not_now))
            }
        },
        modifier = modifier,
    )
}
