/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.interactors.playlist

import javax.inject.Inject
import kotlinx.coroutines.withContext
import com.kuvandikov.base.util.CoroutineDispatchers
import com.kuvandikov.data.Interactor
import com.kuvandikov.data.ResultInteractor
import com.kuvandikov.soundspot.data.repos.playlist.PlaylistsRepo
import com.kuvandikov.soundspot.domain.entities.Playlist
import com.kuvandikov.soundspot.domain.entities.PlaylistAudioIds
import com.kuvandikov.soundspot.domain.entities.PlaylistId
import com.kuvandikov.soundspot.domain.entities.PlaylistItems

class UpdatePlaylist @Inject constructor(
    private val repo: PlaylistsRepo,
    private val dispatchers: CoroutineDispatchers,
) : ResultInteractor<Playlist, Playlist>() {

    override suspend fun doWork(params: Playlist) = withContext(dispatchers.io) {
        return@withContext repo.updatePlaylist(params)
    }
}

class ReorderPlaylist @Inject constructor(
    private val repo: PlaylistsRepo,
    private val dispatchers: CoroutineDispatchers,
) : Interactor<ReorderPlaylist.Params>() {

    data class Params(val playlistId: PlaylistId, val from: Int, val to: Int)

    override suspend fun doWork(params: Params) {
        withContext(dispatchers.io) {
            repo.swapPositions(params.playlistId, params.from, params.to)
        }
    }
}

class UpdatePlaylistItems @Inject constructor(
    private val repo: PlaylistsRepo,
    private val dispatchers: CoroutineDispatchers,
) : Interactor<PlaylistItems>() {

    override suspend fun doWork(params: PlaylistItems) {
        withContext(dispatchers.io) {
            repo.updatePlaylistItems(params)
        }
    }
}

class RemovePlaylistItems @Inject constructor(
    private val repo: PlaylistsRepo,
    private val dispatchers: CoroutineDispatchers,
) : ResultInteractor<PlaylistAudioIds, Int>() {

    override suspend fun doWork(params: PlaylistAudioIds) = withContext(dispatchers.io) {
        return@withContext repo.removePlaylistItems(params)
    }
}
