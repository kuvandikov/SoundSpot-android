/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.library.playlist.addTo

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kuvandikov.datmusic.domain.entities.Playlist
import com.kuvandikov.datmusic.domain.entities.Playlists
import com.kuvandikov.datmusic.ui.coreLibrary.R

internal object NewPlaylistItem {
    private const val ID = -1000L
    private val ITEM @Composable get() = Playlist(id = ID, name = stringResource(R.string.playlist_addTo_new))

    fun Playlist.isNewPlaylistItem() = id == ID

    @Composable
    fun Playlists.withNewPlaylistItem(): Playlists = toMutableList().apply { add(ITEM) }
}
