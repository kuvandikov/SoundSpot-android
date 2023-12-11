/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data.interactors.playlist

import javax.inject.Inject
import kotlinx.coroutines.withContext
import com.kuvandikov.base.util.CoroutineDispatchers
import com.kuvandikov.data.ResultInteractor
import com.kuvandikov.datmusic.data.repos.playlist.PlaylistsRepo
import com.kuvandikov.datmusic.domain.entities.AudioIds
import com.kuvandikov.datmusic.domain.entities.PlaylistId

class AddToPlaylist @Inject constructor(
    private val repo: PlaylistsRepo,
    private val dispatchers: CoroutineDispatchers
) : ResultInteractor<AddToPlaylist.Params, List<PlaylistId>>() {

    data class Params(val playlistId: PlaylistId, var audioIds: AudioIds, val ignoreExisting: Boolean = false)

    override suspend fun doWork(params: Params) = withContext(dispatchers.io) {
        repo.addAudiosToPlaylist(
            playlistId = params.playlistId,
            audioIds = params.audioIds,
            ignoreExisting = params.ignoreExisting
        )
    }
}
