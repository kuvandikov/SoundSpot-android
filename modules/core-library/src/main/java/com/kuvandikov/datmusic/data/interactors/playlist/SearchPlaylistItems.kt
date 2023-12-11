/*
 * Copyright (C) 2022, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data.interactors.playlist

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import com.kuvandikov.data.SubjectInteractor
import com.kuvandikov.datmusic.data.db.daos.AudiosFtsDao
import com.kuvandikov.datmusic.domain.entities.PlaylistId
import com.kuvandikov.datmusic.domain.entities.PlaylistItem

class SearchPlaylistItems @Inject constructor(
    private val audiosFtsDao: AudiosFtsDao,
) : SubjectInteractor<SearchPlaylistItems.Params, List<PlaylistItem>>() {

    data class Params(val playlistId: PlaylistId, val query: String)

    override fun createObservable(params: Params): Flow<List<PlaylistItem>> {
        return audiosFtsDao.searchPlaylist(params.playlistId, "*${params.query}*")
    }
}
