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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.avalokan.R
import com.example.avalokan.ui.theme.AvalokanTheme
import com.example.avalokan.ui.theme.PrimaryLight
import com.example.avalokan.ui.theme.PrimaryTeal
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
    ) { uri -> uri?.toString()?.let(viewModel::setAvatarUri) }

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
    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(bottom = Spacing.extraLarge),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(Modifier.height(Spacing.small))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.sidePadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(36.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
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
                if (avatarUri != null) {
                    AsyncImage(
                        model = avatarUri,
                        contentDescription = "Profile photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(96.dp).clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier.size(96.dp)
                            .background(PrimaryLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.firstOrNull()?.uppercase() ?: "G",
                            style = MaterialTheme.typography.headlineMedium,
                            color = PrimaryTeal
                        )
                    }
                }
                androidx.compose.material3.Surface(
                    shape = CircleShape,
                    color = PrimaryTeal,
                    modifier = Modifier.clickable(onClick = onAvatarClick)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Change profile photo",
                        tint = Color.White,
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
            onClick = onSaveClick,
            shape = StandardButtonShape,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = Spacing.sidePadding)
                .height(48.dp)
        ) {
            Text(text = "Save")
        }
    }
}

@Preview
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
