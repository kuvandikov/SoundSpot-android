/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.audios

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.kuvandikov.base.util.Analytics
import com.kuvandikov.base.util.extensions.simpleName
import com.kuvandikov.common.compose.LocalAnalytics
import com.kuvandikov.soundspot.downloader.Downloader
import com.kuvandikov.soundspot.playback.PlaybackConnection
import com.kuvandikov.soundspot.ui.downloader.LocalDownloader
import com.kuvandikov.soundspot.ui.playback.LocalPlaybackConnection
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun audioActionHandler(
    downloader: Downloader = LocalDownloader.current,
    playbackConnection: PlaybackConnection = LocalPlaybackConnection.current,
    analytics: Analytics = LocalAnalytics.current,
): AudioActionHandler {
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()

    return { action ->
        analytics.event("audio.${action.simpleName}", mapOf("id" to action.audio.id))
        when (action) {
            is AudioItemAction.Play -> playbackConnection.playAudio(action.audio)
            is AudioItemAction.PlayNext -> playbackConnection.playNextAudio(action.audio)
            is AudioItemAction.Download -> coroutine.launch {
                Timber.d("Coroutine launched to download audio: $action")
                downloader.enqueueAudio(action.audio)
            }
            is AudioItemAction.DownloadById -> coroutine.launch {
                Timber.d("Coroutine launched to download audio by Id: $action")
                downloader.enqueueAudio(action.audio.id)
            }
            else -> Timber.e("Unhandled audio action: $action")
        }
    }
}
