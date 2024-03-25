/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.library.playlist.addTo

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kuvandikov.soundspot.domain.entities.Playlist
import com.kuvandikov.soundspot.domain.entities.Playlists
import com.kuvandikov.soundspot.ui.coreLibrary.R

internal object NewPlaylistItem {
    private const val ID = -1000L
    private val ITEM @Composable get() = Playlist(id = ID, name = stringResource(R.string.playlist_addTo_new))

    fun Playlist.isNewPlaylistItem() = id == ID

    @Composable
    fun Playlists.withNewPlaylistItem(): Playlists = toMutableList().apply { add(ITEM) }
}
