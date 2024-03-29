/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.downloads.audio

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import com.kuvandikov.soundspot.domain.entities.AudioDownloadItem
import com.kuvandikov.soundspot.downloader.isComplete
import com.kuvandikov.soundspot.downloader.isIncomplete
import com.kuvandikov.soundspot.downloader.isPausable
import com.kuvandikov.soundspot.downloader.isQueued
import com.kuvandikov.soundspot.downloader.isResumable
import com.kuvandikov.soundspot.ui.audios.AUDIO_SWIPE_ACTION_WEIGHT_MEDIUM
import com.kuvandikov.soundspot.ui.audios.AUDIO_SWIPE_ACTION_WEIGHT_NORMAL
import com.kuvandikov.soundspot.ui.audios.addAudioToPlaylistSwipeAction
import com.kuvandikov.soundspot.ui.audios.addAudioToQueueSwipeAction
import com.kuvandikov.soundspot.ui.downloader.AudioDownloadItemActionHandler
import com.kuvandikov.soundspot.ui.downloader.LocalAudioDownloadItemActionHandler
import com.kuvandikov.soundspot.ui.downloads.AudioDownloadItemAction
import com.kuvandikov.ui.DEFAULT_SWIPE_ACTION_THRESHOLD
import com.kuvandikov.ui.contentColor
import com.kuvandikov.ui.theme.AppTheme
import com.kuvandikov.ui.theme.Blue
import com.kuvandikov.ui.theme.Orange
import com.kuvandikov.ui.theme.Red

@Composable
internal fun AudioDownloadBoxWithSwipeActions(
    audioDownloadItem: AudioDownloadItem,
    onAddToPlaylist: () -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    SwipeableActionsBox(
        swipeThreshold = DEFAULT_SWIPE_ACTION_THRESHOLD,
        content = { content() },
        startActions = listOf(addAudioToQueueSwipeAction(audioDownloadItem.audio)),
        endActions = buildList {
            add(addAudioToPlaylistSwipeAction(onAddToPlaylist, weight = AUDIO_SWIPE_ACTION_WEIGHT_MEDIUM))
            with(audioDownloadItem.downloadInfo) {
                if (isQueued()) {
                    add(pauseAudioDownloadSwipeAction(audioDownloadItem))
                }
                if (isPausable()) {
                    add(pauseAudioDownloadSwipeAction(audioDownloadItem))
                }
                if (isResumable()) {
                    add(resumeAudioDownloadSwipeAction(audioDownloadItem))
                }
                if (isIncomplete()) {
                    add(deleteAudioDownloadSwipeAction(audioDownloadItem))
                }
                if (isComplete()) {
                    add(openAudioDownloadSwipeAction(audioDownloadItem))
                }
                add(deleteAudioDownloadSwipeAction(audioDownloadItem))
            }
        },
    )
}

@Composable
fun openAudioDownloadSwipeAction(
    audioDownloadItem: AudioDownloadItem,
    backgroundColor: Color = Blue,
    actionHandler: AudioDownloadItemActionHandler = LocalAudioDownloadItemActionHandler.current,
) = SwipeAction(
    background = backgroundColor,
    weight = AUDIO_SWIPE_ACTION_WEIGHT_NORMAL,
    icon = {
        Icon(
            modifier = Modifier.padding(AppTheme.specs.padding),
            painter = rememberVectorPainter(Icons.Default.OpenInNew),
            tint = backgroundColor.contentColor(),
            contentDescription = null
        )
    },
    onSwipe = {
        actionHandler(AudioDownloadItemAction.Open(audioDownloadItem))
    },
    isUndo = false,
)

@Composable
fun pauseAudioDownloadSwipeAction(
    audioDownloadItem: AudioDownloadItem,
    backgroundColor: Color = Orange,
    actionHandler: AudioDownloadItemActionHandler = LocalAudioDownloadItemActionHandler.current,
) = SwipeAction(
    background = backgroundColor,
    icon = {
        Icon(
            modifier = Modifier.padding(AppTheme.specs.padding),
            painter = rememberVectorPainter(Icons.Default.Pause),
            tint = backgroundColor.contentColor(),
            contentDescription = null
        )
    },
    onSwipe = {
        actionHandler(AudioDownloadItemAction.Pause(audioDownloadItem))
    },
    isUndo = false,
)

@Composable
fun resumeAudioDownloadSwipeAction(
    audioDownloadItem: AudioDownloadItem,
    backgroundColor: Color = Blue,
    actionHandler: AudioDownloadItemActionHandler = LocalAudioDownloadItemActionHandler.current,
) = SwipeAction(
    background = backgroundColor,
    icon = {
        Icon(
            modifier = Modifier.padding(AppTheme.specs.padding),
            painter = rememberVectorPainter(Icons.Default.PlayArrow),
            tint = backgroundColor.contentColor(),
            contentDescription = null
        )
    },
    onSwipe = {
        actionHandler(AudioDownloadItemAction.Resume(audioDownloadItem))
    },
    isUndo = false,
)

@Composable
fun cancelAudioDownloadSwipeAction(
    audioDownloadItem: AudioDownloadItem,
    backgroundColor: Color = Red,
    actionHandler: AudioDownloadItemActionHandler = LocalAudioDownloadItemActionHandler.current,
) = SwipeAction(
    background = backgroundColor,
    icon = {
        Icon(
            modifier = Modifier.padding(AppTheme.specs.padding),
            painter = rememberVectorPainter(Icons.Default.Cancel),
            tint = backgroundColor.contentColor(),
            contentDescription = null
        )
    },
    onSwipe = {
        actionHandler(AudioDownloadItemAction.Cancel(audioDownloadItem))
    },
    isUndo = false,
)

@Composable
fun removeAudioDownloadSwipeAction(
    audioDownloadItem: AudioDownloadItem,
    backgroundColor: Color = Orange,
    actionHandler: AudioDownloadItemActionHandler = LocalAudioDownloadItemActionHandler.current,
) = SwipeAction(
    background = backgroundColor,
    weight = AUDIO_SWIPE_ACTION_WEIGHT_MEDIUM,
    icon = {
        Icon(
            modifier = Modifier.padding(AppTheme.specs.padding),
            painter = rememberVectorPainter(Icons.Default.Remove),
            tint = backgroundColor.contentColor(),
            contentDescription = null
        )
    },
    onSwipe = {
        actionHandler(AudioDownloadItemAction.Remove(audioDownloadItem))
    },
    isUndo = false,
)

@Composable
fun deleteAudioDownloadSwipeAction(
    audioDownloadItem: AudioDownloadItem,
    backgroundColor: Color = Red,
    actionHandler: AudioDownloadItemActionHandler = LocalAudioDownloadItemActionHandler.current
) = SwipeAction(
    background = backgroundColor,
    icon = {
        Icon(
            modifier = Modifier.padding(AppTheme.specs.padding),
            painter = rememberVectorPainter(Icons.Default.Delete),
            tint = backgroundColor.contentColor(),
            contentDescription = null
        )
    },
    onSwipe = {
        actionHandler(AudioDownloadItemAction.Delete(audioDownloadItem))
    },
    isUndo = false,
)
