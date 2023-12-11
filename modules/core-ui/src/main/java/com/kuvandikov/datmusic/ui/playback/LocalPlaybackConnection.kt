/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.playback

import androidx.compose.runtime.staticCompositionLocalOf
import com.kuvandikov.datmusic.playback.PlaybackConnection

// TODO: Move somewhere else
val LocalPlaybackConnection = staticCompositionLocalOf<PlaybackConnection> {
    error("No LocalPlaybackConnection provided")
}
