/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.library.playlists.edit

import javax.annotation.concurrent.Immutable
import com.kuvandikov.soundspot.domain.entities.Playlist
import com.kuvandikov.i18n.ValidationError

@Immutable
internal data class EditPlaylistViewState(
    val name: String = "",
    val nameError: ValidationError? = null,
    val playlist: Playlist = Playlist(),
    val lastRemovedPlaylistItem: RemovedFromPlaylist? = null,
) {
    companion object {
        val Empty = EditPlaylistViewState()
    }
}

fun interface OnMovePlaylistItem {
    operator fun invoke(from: Int, to: Int)
}
