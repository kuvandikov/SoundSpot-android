/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import com.kuvandikov.base.ui.ThemeState
import com.kuvandikov.base.util.Analytics
import com.kuvandikov.base.util.extensions.stateInDefault
import com.kuvandikov.data.PreferencesStore
import com.kuvandikov.ui.theme.DefaultTheme

object PreferenceKeys {
    const val THEME_STATE_KEY = "theme_state"
}

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val preferences: PreferencesStore,
    private val analytics: Analytics,
) : ViewModel() {

    // Read saved theme state from preferences in a blocking manner (takes ~5 ms)
    // so the app doesn't render first frames with the default theme
    private val savedThemeState = runBlocking {
        preferences.get(PreferenceKeys.THEME_STATE_KEY, ThemeState.serializer(), DefaultTheme).first()
    }
    val themeState = preferences.get(PreferenceKeys.THEME_STATE_KEY, ThemeState.serializer(), DefaultTheme)
        .stateInDefault(viewModelScope, savedThemeState)

    fun applyThemeState(themeState: ThemeState) {
        analytics.event("theme.apply", mapOf("darkMode" to themeState.isDarkMode, "palette" to themeState.colorPalettePreference.name))
        viewModelScope.launch {
            preferences.save(PreferenceKeys.THEME_STATE_KEY, themeState, ThemeState.serializer())
        }
    }
}
