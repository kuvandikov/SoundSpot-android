/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import com.kuvandikov.base.ui.SnackbarManager
import com.kuvandikov.base.ui.SnackbarMessage
import com.kuvandikov.base.util.Analytics
import com.kuvandikov.base.util.extensions.stateInDefault
import com.kuvandikov.base.util.toUiMessage
import com.kuvandikov.soundspot.data.interactors.playlist.DeletePlaylist
import com.kuvandikov.soundspot.data.interactors.playlist.DownloadPlaylist
import com.kuvandikov.soundspot.data.observers.playlist.ObservePlaylists
import com.kuvandikov.soundspot.domain.entities.PlaylistId
import com.kuvandikov.domain.models.Fail
import com.kuvandikov.domain.models.Loading
import com.kuvandikov.domain.models.Params
import com.kuvandikov.domain.models.Success
import com.kuvandikov.i18n.UiMessage
import com.kuvandikov.navigation.Navigator
import com.kuvandikov.navigation.screens.LeafScreen

internal object PlaylistDownloadQueued : SnackbarMessage<Unit>(UiMessage.Resource(R.string.playlist_download_queued))
internal data class PlaylistDownloadQueuedCount(val count: Int) :
    SnackbarMessage<Unit>(UiMessage.Resource(R.string.playlist_download_queuedCount, listOf(count)))

@HiltViewModel
internal class LibraryViewModel @Inject constructor(
    observePlaylists: ObservePlaylists,
    private val playlistDeleter: DeletePlaylist,
    private val playlistDownloader: DownloadPlaylist,
    private val snackbarManager: SnackbarManager,
    private val analytics: Analytics,
    private val navigator: Navigator,
) : ViewModel() {

    val state = observePlaylists.flow
        .map { playlists -> LibraryViewState(items = Success(playlists)) }
        .stateInDefault(viewModelScope, LibraryViewState.Empty)

    init {
        observePlaylists(Params())
    }

    fun onDeletePlaylist(playlistId: PlaylistId) = viewModelScope.launch {
        analytics.event("playlist.row.delete", mapOf("playlistId" to playlistId))
        playlistDeleter.execute(playlistId)
    }

    fun onDownloadPlaylist(playlistId: PlaylistId) = viewModelScope.launch {
        analytics.event("playlist.row.download", mapOf("playlistId" to playlistId))
        playlistDownloader(playlistId).collectLatest { result ->
            when (result) {
                is Fail -> snackbarManager.addMessage(result.error.toUiMessage())
                is Loading -> {
                    snackbarManager.addMessage(PlaylistDownloadQueued)
                    navigator.navigate(LeafScreen.Downloads().createRoute())
                }
                is Success -> {
                    val queuedCount = result()
                    if (queuedCount > 1)
                        snackbarManager.addMessage(PlaylistDownloadQueuedCount(count = queuedCount))
                }
                else -> Unit
            }
        }
    }
}
