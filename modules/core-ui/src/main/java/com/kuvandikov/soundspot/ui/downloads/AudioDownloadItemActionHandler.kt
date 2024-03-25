/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.downloads
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.kuvandikov.base.util.Analytics
import com.kuvandikov.base.util.IntentUtils
import com.kuvandikov.base.util.extensions.simpleName
import com.kuvandikov.common.compose.LocalAnalytics
import com.kuvandikov.soundspot.downloader.Downloader
import com.kuvandikov.soundspot.playback.PlaybackConnection
import com.kuvandikov.soundspot.ui.downloader.AudioDownloadItemActionHandler
import com.kuvandikov.soundspot.ui.downloader.LocalDownloader
import com.kuvandikov.soundspot.ui.playback.LocalPlaybackConnection
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun audioDownloadItemActionHandler(
    downloader: Downloader = LocalDownloader.current,
    playbackConnection: PlaybackConnection = LocalPlaybackConnection.current,
    analytics: Analytics = LocalAnalytics.current,
): AudioDownloadItemActionHandler {
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()

    return { action ->
        analytics.event("downloads.audio.${action.simpleName}", mapOf("id" to action.audio.audio.id))
        coroutine.launch {
            when (action) {
                is AudioDownloadItemAction.Play -> playbackConnection.playAudio(action.audio.audio)
                is AudioDownloadItemAction.PlayNext -> playbackConnection.playNextAudio(action.audio.audio)
                is AudioDownloadItemAction.Pause -> downloader.pause(action.audio)
                is AudioDownloadItemAction.Resume -> downloader.resume(action.audio)
                is AudioDownloadItemAction.Cancel -> downloader.cancel(action.audio)
                is AudioDownloadItemAction.Retry -> downloader.retry(action.audio)
                is AudioDownloadItemAction.Remove -> downloader.remove(action.audio)
                is AudioDownloadItemAction.Delete -> downloader.delete(action.audio)
                is AudioDownloadItemAction.Open -> IntentUtils.openFile(context, action.audio.downloadInfo.fileUri, action.audio.audio.fileMimeType())
                else -> Timber.e("Unhandled action: $action")
            }
        }
    }
}
