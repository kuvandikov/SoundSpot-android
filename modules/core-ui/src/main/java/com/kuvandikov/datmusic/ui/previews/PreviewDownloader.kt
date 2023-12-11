/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.previews

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.tonyodev.fetch2.Status
import java.io.File
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import com.kuvandikov.datmusic.domain.DownloadsSongsGrouping
import com.kuvandikov.datmusic.domain.entities.Audio
import com.kuvandikov.datmusic.domain.entities.AudioDownloadItem
import com.kuvandikov.datmusic.domain.entities.DownloadItem
import com.kuvandikov.datmusic.downloader.Downloader
import com.kuvandikov.datmusic.downloader.DownloaderEvent
import com.kuvandikov.domain.models.Optional

internal object PreviewDownloader : Downloader {
    override val newDownloadId: Flow<String> = flowOf()
    override val downloaderEvents: Flow<DownloaderEvent> = flowOf()
    override fun clearDownloaderEvents() {}
    override fun getDownloaderEvents(): List<DownloaderEvent> = emptyList()

    override suspend fun enqueueAudio(audioId: String): Boolean = true
    override suspend fun enqueueAudio(audio: Audio): Boolean = true

    override suspend fun pause(vararg downloadItems: DownloadItem) {}
    override suspend fun resume(vararg downloadItems: DownloadItem) {}
    override suspend fun cancel(vararg downloadItems: DownloadItem) {}
    override suspend fun retry(vararg downloadItems: DownloadItem) {}
    override suspend fun remove(vararg downloadItems: DownloadItem) {}
    override suspend fun delete(vararg downloadItems: DownloadItem) {}

    override suspend fun findAudioDownload(audioId: String): Optional<Audio> = Optional.None
    override suspend fun getAudioDownload(audioId: String, vararg allowedStatuses: Status): Optional<AudioDownloadItem> = Optional.None

    override val hasDownloadsLocation: Flow<Boolean> = flowOf(true)
    override val downloadsSongsGrouping: Flow<DownloadsSongsGrouping> = flowOf(DownloadsSongsGrouping.ByAlbum)

    override suspend fun setDownloadsSongsGrouping(songsGrouping: DownloadsSongsGrouping) {}
    override suspend fun setDownloadsLocation(folder: File) {}
    override suspend fun setDownloadsLocation(documentFile: DocumentFile) {}
    override suspend fun setDownloadsLocation(uri: Uri?) {}
    override suspend fun resetDownloadsLocation() {}

    override fun requestNewDownloadsLocation() {}
}
