/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.audios

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import kotlinx.coroutines.launch
import timber.log.Timber
import com.kuvandikov.base.util.Analytics
import com.kuvandikov.base.util.extensions.simpleName
import com.kuvandikov.base.util.toast
import com.kuvandikov.common.compose.LocalAnalytics
import com.kuvandikov.datmusic.downloader.Downloader
import com.kuvandikov.datmusic.playback.PlaybackConnection
import com.kuvandikov.datmusic.ui.downloader.LocalDownloader
import com.kuvandikov.datmusic.ui.media.R
import com.kuvandikov.datmusic.ui.playback.LocalPlaybackConnection

@Composable
fun audioActionHandler(
    downloader: Downloader = LocalDownloader.current,
    playbackConnection: PlaybackConnection = LocalPlaybackConnection.current,
    clipboardManager: ClipboardManager = LocalClipboardManager.current,
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
            is AudioItemAction.CopyLink -> {
                clipboardManager.setText(AnnotatedString(action.audio.downloadUrl ?: ""))
                context.toast(R.string.generic_clipboard_copied)
            }
            else -> Timber.e("Unhandled audio action: $action")
        }
    }
}
