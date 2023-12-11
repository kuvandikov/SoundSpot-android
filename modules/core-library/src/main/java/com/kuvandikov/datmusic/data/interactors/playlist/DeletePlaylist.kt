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
import com.kuvandikov.datmusic.domain.entities.PlaylistId

class DeletePlaylist @Inject constructor(
    private val repo: PlaylistsRepo,
    private val dispatchers: CoroutineDispatchers,
) : ResultInteractor<PlaylistId, Boolean>() {

    override suspend fun doWork(params: PlaylistId) = withContext(dispatchers.io) {
        return@withContext repo.delete(params) > 0
    }
}
