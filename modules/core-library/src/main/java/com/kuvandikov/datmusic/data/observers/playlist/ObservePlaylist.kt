/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data.observers.playlist

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import com.kuvandikov.data.SubjectInteractor
import com.kuvandikov.datmusic.data.repos.playlist.PlaylistsRepo
import com.kuvandikov.datmusic.domain.entities.Playlist
import com.kuvandikov.datmusic.domain.entities.PlaylistId

class ObservePlaylist @Inject constructor(
    private val playlistsRepo: PlaylistsRepo
) : SubjectInteractor<PlaylistId, Playlist>() {
    override fun createObservable(params: PlaylistId): Flow<Playlist> = playlistsRepo.playlist(params)
}
