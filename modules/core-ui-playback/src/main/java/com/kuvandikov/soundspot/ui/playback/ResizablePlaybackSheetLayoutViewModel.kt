/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.playback

import androidx.datastore.preferences.core.floatPreferencesKey
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.kuvandikov.base.ui.base.vm.ResizableLayoutViewModel
import com.kuvandikov.base.util.Analytics
import com.kuvandikov.data.PreferencesStore

private val PlaybackSheetLayoutDragOffsetKey = floatPreferencesKey("PlaybackSheetLayoutDragOffsetKey")

@HiltViewModel
class ResizablePlaybackSheetLayoutViewModel @Inject constructor(
    preferencesStore: PreferencesStore,
    analytics: Analytics,
) : ResizableLayoutViewModel(
    preferencesStore = preferencesStore,
    analytics = analytics,
    preferenceKey = PlaybackSheetLayoutDragOffsetKey,
    defaultDragOffset = 0f,
    analyticsPrefix = "playbackSheet.layout"
)
