/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.playback

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.kuvandikov.soundspot.playback.PlaybackConnection

@Composable
fun PlaybackHost(content: @Composable () -> Unit) {
    PlaybackHost(
        playbackConnection = hiltViewModel<PlaybackConnectionViewModel>().playbackConnection,
        content = content,
    )
}

@Composable
private fun PlaybackHost(
    playbackConnection: PlaybackConnection,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalPlaybackConnection provides playbackConnection) { content() }
}
