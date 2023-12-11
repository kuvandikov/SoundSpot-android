/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.library.playlist.addTo

import com.kuvandikov.base.ui.SnackbarAction
import com.kuvandikov.base.ui.SnackbarMessage
import com.kuvandikov.datmusic.domain.entities.Playlist
import com.kuvandikov.datmusic.domain.entities.PlaylistId
import com.kuvandikov.datmusic.ui.coreLibrary.R
import com.kuvandikov.i18n.UiMessage

data class AddedToPlaylistMessage(val playlist: Playlist) :
    SnackbarMessage<PlaylistId>(
        message = UiMessage.Resource(
            R.string.playlist_addTo_added,
            formatArgs = listOf(playlist.name)
        ),
        action = SnackbarAction(UiMessage.Resource(R.string.playlist_addTo_open), playlist.id)
    )
