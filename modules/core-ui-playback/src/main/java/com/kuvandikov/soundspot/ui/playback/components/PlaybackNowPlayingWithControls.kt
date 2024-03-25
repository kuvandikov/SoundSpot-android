/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.playback.components

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.PauseCircleFilled
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOn
import androidx.compose.material.icons.filled.RepeatOneOn
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kuvandikov.base.util.extensions.orNA
import com.kuvandikov.common.compose.rememberFlowWithLifecycle
import com.kuvandikov.soundspot.playback.PlaybackConnection
import com.kuvandikov.soundspot.playback.artist
import com.kuvandikov.soundspot.playback.hasNext
import com.kuvandikov.soundspot.playback.hasPrevious
import com.kuvandikov.soundspot.playback.isError
import com.kuvandikov.soundspot.playback.isPlayEnabled
import com.kuvandikov.soundspot.playback.isPlaying
import com.kuvandikov.soundspot.playback.playPause
import com.kuvandikov.soundspot.playback.title
import com.kuvandikov.soundspot.playback.toggleRepeatMode
import com.kuvandikov.soundspot.playback.toggleShuffleMode
import com.kuvandikov.soundspot.ui.playback.LocalPlaybackConnection
import com.kuvandikov.soundspot.ui.previews.PreviewSoundspotCore
import com.kuvandikov.ui.components.IconButton
import com.kuvandikov.ui.material.ContentAlpha
import com.kuvandikov.ui.material.ProvideContentAlpha
import com.kuvandikov.ui.simpleClickable
import com.kuvandikov.ui.theme.AppTheme
import com.kuvandikov.ui.theme.Theme
import com.kuvandikov.ui.theme.disabledAlpha

object PlaybackNowPlayingDefaults {
    val titleTextStyle @Composable get() = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
    val artistTextStyle @Composable get() = MaterialTheme.typography.titleMedium
}

@Composable
internal fun PlaybackNowPlayingWithControls(
    nowPlaying: MediaMetadataCompat,
    playbackState: PlaybackStateCompat,
    contentColor: Color,
    onTitleClick: () -> Unit,
    onArtistClick: () -> Unit,
    modifier: Modifier = Modifier,
    titleTextStyle: TextStyle = PlaybackNowPlayingDefaults.titleTextStyle,
    artistTextStyle: TextStyle = PlaybackNowPlayingDefaults.artistTextStyle,
    onlyControls: Boolean = false,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(AppTheme.specs.paddingLarge)
    ) {
        if (!onlyControls)
            PlaybackNowPlaying(
                nowPlaying = nowPlaying,
                onTitleClick = onTitleClick,
                onArtistClick = onArtistClick,
                titleTextStyle = titleTextStyle,
                artistTextStyle = artistTextStyle,
            )

        PlaybackProgress(
            playbackState = playbackState,
            contentColor = contentColor
        )

        PlaybackControls(
            playbackState = playbackState,
            contentColor = contentColor,
        )
    }
}

@Composable
internal fun PlaybackNowPlaying(
    nowPlaying: MediaMetadataCompat,
    onTitleClick: () -> Unit,
    onArtistClick: () -> Unit,
    modifier: Modifier = Modifier,
    titleTextStyle: TextStyle = PlaybackNowPlayingDefaults.titleTextStyle,
    artistTextStyle: TextStyle = PlaybackNowPlayingDefaults.artistTextStyle,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
) {
    Column(
        horizontalAlignment = horizontalAlignment,
        modifier = modifier
    ) {
        Text(
            text = nowPlaying.title.orNA(),
            style = titleTextStyle,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.simpleClickable(onClick = onTitleClick)
        )
        ProvideContentAlpha(ContentAlpha.medium) {
            Text(
                text = nowPlaying.artist.orNA(),
                style = artistTextStyle,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.simpleClickable(onClick = onArtistClick)
            )
        }
    }
}

@Composable
internal fun PlaybackControls(
    playbackState: PlaybackStateCompat,
    contentColor: Color,
    modifier: Modifier = Modifier,
    smallRippleRadius: Dp = 30.dp,
    playbackConnection: PlaybackConnection = LocalPlaybackConnection.current
) {
    val playbackMode by rememberFlowWithLifecycle(playbackConnection.playbackMode)
    Row(
        modifier = modifier.width(288.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = { playbackConnection.mediaController?.toggleShuffleMode() },
            modifier = Modifier
                .size(20.dp)
                .weight(2f),
            rippleRadius = smallRippleRadius,
        ) {
            Icon(
                painter = rememberVectorPainter(
                    when (playbackMode.shuffleMode) {
                        PlaybackStateCompat.SHUFFLE_MODE_NONE -> Icons.Default.Shuffle
                        PlaybackStateCompat.SHUFFLE_MODE_ALL -> Icons.Default.ShuffleOn
                        else -> Icons.Default.Shuffle
                    }
                ),
                tint = contentColor,
                modifier = Modifier.fillMaxSize(),
                contentDescription = null
            )
        }

        Spacer(Modifier.width(AppTheme.specs.paddingLarge))

        IconButton(
            onClick = { playbackConnection.transportControls?.skipToPrevious() },
            modifier = Modifier
                .size(40.dp)
                .weight(4f),
            rippleRadius = smallRippleRadius,
        ) {
            Icon(
                painter = rememberVectorPainter(Icons.Default.SkipPrevious),
                tint = contentColor.disabledAlpha(playbackState.hasPrevious),
                modifier = Modifier.fillMaxSize(),
                contentDescription = null
            )
        }

        Spacer(Modifier.width(AppTheme.specs.padding))

        IconButton(
            onClick = { playbackConnection.mediaController?.playPause() },
            modifier = Modifier
                .size(80.dp)
                .weight(8f),
            rippleRadius = 35.dp,
        ) {
            Icon(
                painter = rememberVectorPainter(
                    when {
                        playbackState.isError -> Icons.Filled.ErrorOutline
                        playbackState.isPlaying -> Icons.Filled.PauseCircleFilled
                        playbackState.isPlayEnabled -> Icons.Filled.PlayCircleFilled
                        else -> Icons.Filled.PlayCircleFilled
                    }
                ),
                tint = contentColor,
                modifier = Modifier.fillMaxSize(),
                contentDescription = null
            )
        }

        Spacer(Modifier.width(AppTheme.specs.padding))

        IconButton(
            onClick = { playbackConnection.transportControls?.skipToNext() },
            modifier = Modifier
                .size(40.dp)
                .weight(4f),
            rippleRadius = smallRippleRadius,
        ) {
            Icon(
                painter = rememberVectorPainter(Icons.Default.SkipNext),
                tint = contentColor.disabledAlpha(playbackState.hasNext),
                modifier = Modifier.fillMaxSize(),
                contentDescription = null
            )
        }

        Spacer(Modifier.width(AppTheme.specs.paddingLarge))

        IconButton(
            onClick = { playbackConnection.mediaController?.toggleRepeatMode() },
            modifier = Modifier
                .size(20.dp)
                .weight(2f),
            rippleRadius = smallRippleRadius,
        ) {
            Icon(
                painter = rememberVectorPainter(
                    when (playbackMode.repeatMode) {
                        PlaybackStateCompat.REPEAT_MODE_ONE -> Icons.Default.RepeatOneOn
                        PlaybackStateCompat.REPEAT_MODE_ALL -> Icons.Default.RepeatOn
                        else -> Icons.Default.Repeat
                    }
                ),
                tint = contentColor,
                modifier = Modifier.fillMaxSize(),
                contentDescription = null
            )
        }
    }
}

@Preview
@Composable
fun PlaybackNowPlayingWithControlsPreview() = PreviewSoundspotCore {
    val playbackConnection = LocalPlaybackConnection.current
    val nowPlaying by rememberFlowWithLifecycle(playbackConnection.nowPlaying)
    val playbackState by rememberFlowWithLifecycle(playbackConnection.playbackState)

    PlaybackNowPlayingWithControls(
        nowPlaying = nowPlaying,
        playbackState = playbackState,
        contentColor = Theme.colorScheme.onSurface,
        onTitleClick = {},
        onArtistClick = {},
    )
}
