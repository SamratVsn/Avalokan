package com.example.avalokan.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.local.PreferencesStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Startup-only state. Keep this dependency graph small: the Activity needs the
 * theme before it can draw, and pulling in repositories here would initialize
 * Room and Firestore on the critical startup path.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    userPreferences: PreferencesStore,
) : ViewModel() {
    val themeChoice: StateFlow<String> = userPreferences.themeChoice
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            PreferencesStore.THEME_SYSTEM,
        )
}
