/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.library.playlist.addTo

import kotlinx.coroutines.flow.StateFlow
import com.kuvandikov.datmusic.domain.entities.AudioIds
import com.kuvandikov.datmusic.domain.entities.Playlist

interface AddToPlaylistViewModel {
    val playlists: StateFlow<List<Playlist>>
    fun addTo(playlist: Playlist, audioIds: AudioIds)
}
