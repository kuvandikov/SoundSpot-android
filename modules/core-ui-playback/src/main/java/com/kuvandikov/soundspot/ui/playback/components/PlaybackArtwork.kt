/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.playback.components

import android.net.Uri
import android.support.v4.media.MediaMetadataCompat
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import com.kuvandikov.base.util.extensions.Callback
import com.kuvandikov.common.compose.rememberFlowWithLifecycle
import com.kuvandikov.soundspot.playback.PlaybackConnection
import com.kuvandikov.soundspot.playback.artwork
import com.kuvandikov.soundspot.playback.playPause
import com.kuvandikov.soundspot.ui.playback.LocalPlaybackConnection
import com.kuvandikov.ui.AdaptiveColorResult
import com.kuvandikov.ui.adaptiveColor
import com.kuvandikov.ui.coloredRippleClickable
import com.kuvandikov.ui.components.CoverImage
import com.kuvandikov.ui.theme.AppTheme
import com.kuvandikov.ui.theme.plainSurfaceColor

@Composable
internal fun PlaybackArtwork(
    artwork: Uri,
    contentColor: Color,
    nowPlaying: MediaMetadataCompat,
    modifier: Modifier = Modifier,
    onClick: Callback? = null,
    playbackConnection: PlaybackConnection = LocalPlaybackConnection.current,
) {
    CoverImage(
        data = artwork,
        shape = RectangleShape,
        backgroundColor = plainSurfaceColor(),
        contentColor = contentColor,
        bitmapPlaceholder = nowPlaying.artwork,
        modifier = Modifier
            .padding(horizontal = AppTheme.specs.paddingLarge)
            .then(modifier),
        imageModifier = Modifier.coloredRippleClickable(
            onClick = {
                if (onClick != null) onClick.invoke()
                else playbackConnection.mediaController?.playPause()
            },
            color = contentColor,
            rippleRadius = Dp.Unspecified,
        ),
    )
}

@Composable
fun nowPlayingArtworkAdaptiveColor(
    playbackConnection: PlaybackConnection = LocalPlaybackConnection.current
): State<AdaptiveColorResult> {
    val nowPlaying by rememberFlowWithLifecycle(playbackConnection.nowPlaying)
    return adaptiveColor(nowPlaying.artwork, initial = MaterialTheme.colorScheme.background)
}
