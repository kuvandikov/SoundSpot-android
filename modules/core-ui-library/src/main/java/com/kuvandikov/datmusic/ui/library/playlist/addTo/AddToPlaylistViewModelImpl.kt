/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.library.playlist.addTo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import com.kuvandikov.base.ui.SnackbarManager
import com.kuvandikov.base.util.Analytics
import com.kuvandikov.base.util.extensions.stateInDefault
import com.kuvandikov.datmusic.data.interactors.playlist.AddToPlaylist
import com.kuvandikov.datmusic.data.interactors.playlist.CreatePlaylist
import com.kuvandikov.datmusic.data.observers.playlist.ObservePlaylists
import com.kuvandikov.datmusic.domain.entities.AudioIds
import com.kuvandikov.datmusic.domain.entities.Playlist
import com.kuvandikov.datmusic.ui.library.playlist.addTo.NewPlaylistItem.isNewPlaylistItem
import com.kuvandikov.domain.models.Params
import com.kuvandikov.navigation.Navigator
import com.kuvandikov.navigation.screens.LeafScreen

@HiltViewModel
internal class AddToPlaylistViewModelImpl @Inject constructor(
    observePlaylists: ObservePlaylists,
    private val addToPlaylist: AddToPlaylist,
    private val createPlaylist: CreatePlaylist,
    private val snackbarManager: SnackbarManager,
    private val analytics: Analytics,
    private val navigator: Navigator,
) : AddToPlaylistViewModel, ViewModel() {

    override val playlists = observePlaylists.flow.stateInDefault(viewModelScope, emptyList())

    init {
        observePlaylists(Params())
    }

    override fun addTo(playlist: Playlist, audioIds: AudioIds) {
        analytics.event("playlists.addTo", mapOf("playlistId" to playlist.id, "audiosIds" to audioIds.joinToString()))
        viewModelScope.launch {
            var targetPlaylist = playlist
            if (playlist.isNewPlaylistItem()) {
                targetPlaylist = createPlaylist.execute(CreatePlaylist.Params(generateNameIfEmpty = true))
            }

            addToPlaylist.execute(AddToPlaylist.Params(targetPlaylist.id, audioIds))

            val addedToPlaylist = AddedToPlaylistMessage(targetPlaylist)
            snackbarManager.addMessage(addedToPlaylist)
            if (snackbarManager.observeMessageAction(addedToPlaylist) != null)
                navigator.navigate(LeafScreen.PlaylistDetail.buildRoute(targetPlaylist.id))
        }
    }
}
