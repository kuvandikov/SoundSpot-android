/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data.interactors.playlist

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import com.kuvandikov.base.imageloading.getBitmap
import com.kuvandikov.base.util.CoroutineDispatchers
import com.kuvandikov.data.ResultInteractor
import com.kuvandikov.datmusic.data.repos.playlist.ArtworkImageFileType
import com.kuvandikov.datmusic.data.repos.playlist.PlaylistArtworkUtils.savePlaylistArtwork
import com.kuvandikov.datmusic.data.repos.playlist.PlaylistsRepo
import com.kuvandikov.datmusic.domain.entities.PlaylistId
import com.kuvandikov.i18n.LoadingError

class SetCustomPlaylistArtwork @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val repo: PlaylistsRepo,
    private val dispatchers: CoroutineDispatchers,
) : ResultInteractor<SetCustomPlaylistArtwork.Params, Unit>() {

    data class Params(val playlistId: PlaylistId, val uri: Uri)

    override suspend fun doWork(params: Params) = withContext(dispatchers.io) {
        repo.validatePlaylistId(params.playlistId)
        val bitmap = appContext.getBitmap(params.uri, allowHardware = false) ?: throw LoadingError
        val artwork = params.playlistId.savePlaylistArtwork(appContext, bitmap, ArtworkImageFileType.PLAYLIST_USER_SET, recycle = false)

        val playlist = repo.playlist(params.playlistId).first()
        repo.updatePlaylist(playlist.copy(artworkPath = artwork.path, artworkSource = artwork.path.hashCode().toString()))
        return@withContext
    }
}

class ClearPlaylistArtwork @Inject constructor(
    private val repo: PlaylistsRepo,
    private val dispatchers: CoroutineDispatchers,
) : ResultInteractor<PlaylistId, Unit>() {

    override suspend fun doWork(params: PlaylistId) = withContext(dispatchers.io) {
        repo.clearPlaylistArtwork(params)
        return@withContext
    }
}
