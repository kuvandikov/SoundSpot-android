/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data.observers.playlist

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import com.kuvandikov.data.SubjectInteractor
import com.kuvandikov.datmusic.data.repos.playlist.PlaylistsRepo
import com.kuvandikov.datmusic.domain.entities.Playlists
import com.kuvandikov.domain.models.Params

class ObservePlaylists @Inject constructor(
    private val playlistsRepo: PlaylistsRepo
) : SubjectInteractor<Params, Playlists>() {
    override fun createObservable(params: Params): Flow<Playlists> = playlistsRepo.playlists()
}
