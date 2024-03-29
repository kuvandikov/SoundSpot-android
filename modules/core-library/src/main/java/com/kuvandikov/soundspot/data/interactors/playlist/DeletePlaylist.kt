/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.interactors.playlist

import javax.inject.Inject
import kotlinx.coroutines.withContext
import com.kuvandikov.base.util.CoroutineDispatchers
import com.kuvandikov.data.ResultInteractor
import com.kuvandikov.soundspot.data.repos.playlist.PlaylistsRepo
import com.kuvandikov.soundspot.domain.entities.PlaylistId

class DeletePlaylist @Inject constructor(
    private val repo: PlaylistsRepo,
    private val dispatchers: CoroutineDispatchers,
) : ResultInteractor<PlaylistId, Boolean>() {

    override suspend fun doWork(params: PlaylistId) = withContext(dispatchers.io) {
        return@withContext repo.delete(params) > 0
    }
}
