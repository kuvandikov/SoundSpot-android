/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.library.playlist.addTo

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.kuvandikov.datmusic.domain.entities.AudioIds
import com.kuvandikov.datmusic.domain.entities.Playlist

object PreviewAddToPlaylistViewModel : AddToPlaylistViewModel {
    private val previewPlaylists = MutableStateFlow(listOf(Playlist(name = "Preview Playlist")))
    override val playlists: StateFlow<List<Playlist>> = previewPlaylists
    override fun addTo(playlist: Playlist, audioIds: AudioIds) {}
}
