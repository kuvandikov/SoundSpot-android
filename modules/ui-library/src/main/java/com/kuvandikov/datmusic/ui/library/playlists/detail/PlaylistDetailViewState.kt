/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.library.playlists.detail

import android.content.Context
import javax.annotation.concurrent.Immutable
import com.kuvandikov.datmusic.data.observers.playlist.ObservePlaylistDetails
import com.kuvandikov.datmusic.domain.entities.Playlist
import com.kuvandikov.datmusic.domain.entities.PlaylistItems
import com.kuvandikov.datmusic.ui.detail.MediaDetailViewState
import com.kuvandikov.datmusic.ui.utils.AudiosCountDuration
import com.kuvandikov.domain.models.Async
import com.kuvandikov.domain.models.Success
import com.kuvandikov.domain.models.Uninitialized

@Immutable
internal data class PlaylistDetailViewState(
    val playlist: Playlist? = null,
    val playlistDetails: Async<PlaylistItems> = Uninitialized,
    val params: ObservePlaylistDetails.Params = ObservePlaylistDetails.Params(),
    val audiosCountDuration: AudiosCountDuration? = null,
) : MediaDetailViewState<PlaylistItems> {

    override val isLoaded = playlist != null
    override val isEmpty = playlistDetails is Success && playlistDetails.invoke().isEmpty()
    override val title = playlist?.name

    override fun artwork(context: Context) = playlist?.artworkFile()
    override fun details() = playlistDetails

    companion object {
        val Empty = PlaylistDetailViewState()
    }
}
