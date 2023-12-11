/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.home

import androidx.datastore.preferences.core.floatPreferencesKey
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.kuvandikov.base.ui.base.vm.ResizableLayoutViewModel
import com.kuvandikov.base.util.Analytics
import com.kuvandikov.data.PreferencesStore

private val HomeNavigationRailDragOffsetKey = floatPreferencesKey("HomeNavigationRailWeightKey")

@HiltViewModel
class ResizableHomeNavigationRailViewModel @Inject constructor(
    preferencesStore: PreferencesStore,
    analytics: Analytics,
) : ResizableLayoutViewModel(
    preferencesStore = preferencesStore,
    analytics = analytics,
    preferenceKey = HomeNavigationRailDragOffsetKey,
    defaultDragOffset = 0f,
    analyticsPrefix = "home.navigationRail"
)
