package com.example.avalokan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.avalokan.data.local.UserPreferences
import com.example.avalokan.ui.settings.SettingsViewModel
import com.example.avalokan.ui.theme.AvalokanTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by settingsViewModel.uiState.collectAsState()
            val systemDark = isSystemInDarkTheme()
            AvalokanTheme(
                darkTheme = when (uiState.themeChoice) {
                    UserPreferences.THEME_LIGHT -> false
                    UserPreferences.THEME_DARK -> true
                    else -> systemDark
                }
            ) {
                AvalokanApp()
            }
        }
    }
}
