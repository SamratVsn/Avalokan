package com.example.avalokan.ui.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.avalokan.R
import com.example.avalokan.data.local.UserPreferences
import com.example.avalokan.ui.theme.AccentMarigold
import com.example.avalokan.ui.theme.AvalokanTheme
import com.example.avalokan.ui.theme.PrimaryLight
import com.example.avalokan.ui.theme.PrimaryTeal
import com.example.avalokan.ui.theme.Spacing
import com.example.avalokan.ui.theme.StandardCardShape

private val DangerRed = Color(0xFFE53935)
private val DangerBg = Color(0xFFFFEBEE)
private val WarmBg = Color(0xFFFFF3E0)
private val NeutralBg = Color(0xFFF5F5F5)

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> viewModel.onNotificationsToggle(granted) }

    fun hasNotificationPermission(): Boolean =
        Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

    SettingsContent(
        uiState = uiState,
        notificationsGranted = hasNotificationPermission(),
        onNotificationsChange = { wantOn ->
            if (!wantOn || hasNotificationPermission()) {
                viewModel.onNotificationsToggle(wantOn)
            } else {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        },
        onThemeCycle = {
            val next = when (uiState.themeChoice) {
                UserPreferences.THEME_SYSTEM -> UserPreferences.THEME_LIGHT
                UserPreferences.THEME_LIGHT -> UserPreferences.THEME_DARK
                else -> UserPreferences.THEME_SYSTEM
            }
            viewModel.onThemeChoice(next)
        },
        onClearCache = viewModel::clearCachedData,
        onResetSettings = viewModel::resetSettings,
        onBackClick = onBackClick,
        onEditProfileClick = onEditProfileClick
    )
}

@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    notificationsGranted: Boolean,
    onNotificationsChange: (Boolean) -> Unit,
    onThemeCycle: () -> Unit,
    onBackClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onClearCache: () -> Unit,
    onResetSettings: () -> Unit
) {
    var showClearDialog by rememberSaveable { mutableStateOf(false) }
    var showResetDialog by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(bottom = Spacing.extraLarge)
    ) {
        Spacer(Modifier.height(Spacing.extraSmall))
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
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(12.dp))
        Column(
            modifier = Modifier.weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AccountSetting(
                notificationsChecked = uiState.notificationsEnabled && notificationsGranted,
                onNotificationsChange = onNotificationsChange,
                onEditProfileClick = onEditProfileClick,
                modifier = Modifier.padding(horizontal = Spacing.sidePadding)
            )
            AppearanceSection(
                themeChoice = uiState.themeChoice,
                onThemeCycle = onThemeCycle,
                modifier = Modifier.padding(horizontal = Spacing.sidePadding)
            )
            SuppNFeed(modifier = Modifier.padding(horizontal = Spacing.sidePadding))
            StorageSection(
                onClearCache = { showClearDialog = true },
                onResetSettings = { showResetDialog = true },
                modifier = Modifier.padding(horizontal = Spacing.sidePadding)
            )
            SettingCard(
                icon = Icons.AutoMirrored.Filled.Logout,
                iconBg = DangerBg,
                iconTint = DangerRed,
                settingName = stringResource(R.string.logOut),
                nameColor = DangerRed,
                modifier = Modifier.padding(horizontal = Spacing.sidePadding)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Crafted with ❤ for Nepal's Heritage",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = "Version 1.0.4",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        if (showClearDialog) {
            AlertDialog(
                onDismissRequest = { showClearDialog = false },
                title = { Text(text = "Clear cached data?") },
                text = { Text(text = "Downloaded places and events will be removed and re-fetched from the server.") },
                confirmButton = {
                    TextButton(onClick = {
                        showClearDialog = false
                        onClearCache()
                    }) { Text(text = "Clear") }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDialog = false }) { Text(text = "Cancel") }
                }
            )
        }
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text(text = "Reset all settings?") },
                text = { Text(text = "Theme, notifications, avatar and saved places return to defaults.") },
                confirmButton = {
                    TextButton(onClick = {
                        showResetDialog = false
                        onResetSettings()
                    }) { Text(text = "Reset") }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) { Text(text = "Cancel") }
                }
            )
        }
    }
}

@Composable
private fun StorageSection(
    onClearCache: () -> Unit,
    onResetSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Storage & Data".uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Card(
            shape = StandardCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column {
                SettingRow(
                    icon = Icons.Default.Delete,
                    iconBg = DangerBg,
                    iconTint = DangerRed,
                    name = "Clear cached data",
                    onClick = onClearCache
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
                SettingRow(
                    icon = Icons.Default.Refresh,
                    iconBg = DangerBg,
                    iconTint = DangerRed,
                    name = "Reset all settings",
                    onClick = onResetSettings
                )
            }
        }
    }
}

@Composable
private fun AccountSetting(
    notificationsChecked: Boolean,
    onNotificationsChange: (Boolean) -> Unit,
    onEditProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.account).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Card(
            shape = StandardCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column {
                SettingRow(
                    icon = Icons.Default.Person,
                    iconBg = PrimaryLight,
                    iconTint = PrimaryTeal,
                    name = stringResource(R.string.editProfile),
                    onClick = onEditProfileClick
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
                SettingRow(
                    icon = Icons.Default.Shield,
                    iconBg = PrimaryLight,
                    iconTint = PrimaryTeal,
                    name = stringResource(R.string.security)
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier.size(32.dp)
                            .background(color = PrimaryLight, shape = RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            tint = PrimaryTeal,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = stringResource(R.string.notifications),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = notificationsChecked,
                        onCheckedChange = onNotificationsChange
                    )
                }
            }
        }
    }
}

@Composable
private fun AppearanceSection(
    themeChoice: String,
    onThemeCycle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val label = when (themeChoice) {
        UserPreferences.THEME_LIGHT -> "Light"
        UserPreferences.THEME_DARK -> "Dark"
        else -> "System"
    }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Appearance".uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Card(
            shape = StandardCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .clickable(onClick = onThemeCycle)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier.size(32.dp)
                        .background(color = PrimaryLight, shape = RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Palette,
                        contentDescription = null,
                        tint = PrimaryTeal,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = "Theme",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun SuppNFeed(modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.suppNFe).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Card(
            shape = StandardCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column {
                SettingRow(
                    icon = Icons.Default.Lightbulb,
                    iconBg = WarmBg,
                    iconTint = AccentMarigold,
                    name = stringResource(R.string.sendSugRep)
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
                SettingRow(
                    icon = Icons.Default.Info,
                    iconBg = NeutralBg,
                    iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                    name = stringResource(R.string.appVersion),
                    trailing = "1.0.0"
                )
            }
        }
    }
}

@Composable
private fun SettingCard(
    icon: ImageVector,
    settingName: String,
    modifier: Modifier = Modifier,
    iconBg: Color = DangerBg,
    iconTint: Color = DangerRed,
    nameColor: Color = DangerRed
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = StandardCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(32.dp)
                    .background(color = iconBg, shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(16.dp))
            }
            Text(
                text = settingName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = nameColor,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    name: String,
    trailing: String? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(32.dp)
                .background(color = iconBg, shape = RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(16.dp))
        }
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        if (trailing != null) {
            Text(
                text = trailing,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}

@Preview
@Composable
private fun SettingsPreview() {
    AvalokanTheme {
        SettingsContent(
            uiState = SettingsUiState(),
            notificationsGranted = true,
            onNotificationsChange = {},
            onThemeCycle = {},
            onBackClick = {},
            onEditProfileClick = {},
            onClearCache = {},
            onResetSettings = {}
        )
    }
}
