/*
 * Copyright (C) 2022, Alashov Berkeli
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data.observers.playlist

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import com.kuvandikov.data.SubjectInteractor
import com.kuvandikov.datmusic.data.repos.playlist.PlaylistsRepo
import com.kuvandikov.datmusic.domain.entities.PlaylistId

class ObservePlaylistExistence @Inject constructor(
    private val playlistsRepo: PlaylistsRepo
) : SubjectInteractor<PlaylistId, Boolean>() {
    override fun createObservable(params: PlaylistId): Flow<Boolean> = playlistsRepo.has(params)
}
