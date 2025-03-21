/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.interactors.playlist

import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import com.kuvandikov.base.util.CoroutineDispatchers
import com.kuvandikov.data.AsyncInteractor
import com.kuvandikov.soundspot.coreLibrary.R
import com.kuvandikov.soundspot.data.repos.playlist.PlaylistsRepo
import com.kuvandikov.soundspot.domain.entities.PlaylistId
import com.kuvandikov.soundspot.downloader.Downloader
import com.kuvandikov.soundspot.downloader.DownloaderEventsError
import com.kuvandikov.i18n.UiMessage
import com.kuvandikov.i18n.ValidationError

object PlaylistIsEmpty : ValidationError(UiMessage.Resource(R.string.playlist_download_error_empty))

class DownloadPlaylist @Inject constructor(
    private val repo: PlaylistsRepo,
    private val downloader: Downloader,
    private val dispatchers: CoroutineDispatchers,
) : AsyncInteractor<PlaylistId, Int>() {

    override suspend fun prepare(params: PlaylistId) {
        downloader.clearDownloaderEvents()
        if (repo.playlistItems(params).first().isEmpty())
            throw PlaylistIsEmpty.error()
    }

    override suspend fun doWork(params: PlaylistId) = withContext(dispatchers.io) {
        val audios = repo.playlistItems(params).first().map { it.audio }
        var enqueuedCount = 0

        audios.forEach {
            if (downloader.enqueueAudio(it))
                enqueuedCount++
        }

        val events = downloader.getDownloaderEvents()
        if (enqueuedCount == 0 && events.isNotEmpty())
            throw DownloaderEventsError(events)

        return@withContext enqueuedCount
    }
}
