package com.example.avalokan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.avalokan.data.local.PreferencesStore
import com.example.avalokan.ui.MainViewModel
import com.example.avalokan.ui.theme.AvalokanTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        var keepSplash = true
        splashScreen.setKeepOnScreenCondition { keepSplash }
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({ keepSplash = false }, 1000L)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeChoice by mainViewModel.themeChoice.collectAsState()
            val systemDark = isSystemInDarkTheme()
            AvalokanTheme(
                darkTheme = when (themeChoice) {
                    PreferencesStore.THEME_LIGHT -> false
                    PreferencesStore.THEME_DARK -> true
                    else -> systemDark
                }
            ) {
                AvalokanApp()
            }
        }
    }
}
