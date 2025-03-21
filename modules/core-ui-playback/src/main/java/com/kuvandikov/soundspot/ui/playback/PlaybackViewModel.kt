/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.playback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuvandikov.base.ui.SnackbarAction
import com.kuvandikov.base.ui.SnackbarManager
import com.kuvandikov.base.ui.SnackbarMessage
import com.kuvandikov.base.util.Analytics
import com.kuvandikov.base.util.toUiMessage
import com.kuvandikov.soundspot.data.SoundspotSearchParams
import com.kuvandikov.soundspot.data.interactors.playlist.CreatePlaylist
import com.kuvandikov.soundspot.domain.entities.Playlist
import com.kuvandikov.soundspot.domain.entities.PlaylistId
import com.kuvandikov.soundspot.playback.PlaybackConnection
import com.kuvandikov.soundspot.playback.models.MEDIA_TYPE_ALBUM
import com.kuvandikov.soundspot.playback.models.MEDIA_TYPE_ARTIST
import com.kuvandikov.soundspot.playback.models.MEDIA_TYPE_AUDIO_QUERY
import com.kuvandikov.soundspot.playback.models.MEDIA_TYPE_DOWNLOADS
import com.kuvandikov.soundspot.playback.models.MEDIA_TYPE_PLAYLIST
import com.kuvandikov.soundspot.playback.models.QueueTitle.Companion.asQueueTitle
import com.kuvandikov.soundspot.playback.models.toAlbumSearchQuery
import com.kuvandikov.soundspot.playback.models.toArtistSearchQuery
import com.kuvandikov.soundspot.ui.coreLibrary.R
import com.kuvandikov.i18n.UiMessage
import com.kuvandikov.navigation.Navigator
import com.kuvandikov.navigation.screens.LeafScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SavedAsPlaylistMessage(val playlist: Playlist) :
    SnackbarMessage<PlaylistId>(
        message = UiMessage.Resource(R.string.playback_queue_saveAsPlaylist_saved, formatArgs = listOf(playlist.name)),
        action = SnackbarAction(UiMessage.Resource(R.string.playback_queue_saveAsPlaylist_open), playlist.id)
    )

@HiltViewModel
class PlaybackViewModel @Inject constructor(
    private val playbackConnection: PlaybackConnection,
    private val createPlaylist: CreatePlaylist,
    private val snackbarManager: SnackbarManager,
    private val navigator: Navigator,
    private val analytics: Analytics,
) : ViewModel() {

    fun onSaveQueueAsPlaylist() = viewModelScope.launch {
        val queue = playbackConnection.playbackQueue.first()
        analytics.event(
            "playbackSheet.saveQueueAsPlaylist",
            mapOf("count" to queue.size, "queue" to queue.title)
        )

        val params = CreatePlaylist.Params(name = queue.title.asQueueTitle().localizeValue(), audios = queue, trimIfTooLong = true)
        createPlaylist(params)
            .catch { snackbarManager.addMessage(it.toUiMessage()) }
            .collectLatest { playlist ->
                val savedAsPlaylist = SavedAsPlaylistMessage(playlist)
                snackbarManager.addMessage(savedAsPlaylist)
                if (snackbarManager.observeMessageAction(savedAsPlaylist) != null)
                    navigator.navigate(LeafScreen.PlaylistDetail.buildRoute(playlist.id))
            }
    }

    fun onNavigateToQueueSource() = viewModelScope.launch {
        val queue = playbackConnection.playbackQueue.first()
        val (sourceMediaType, sourceMediaValue) = queue.title.asQueueTitle().sourceMediaId
        analytics.event(
            "playbackSheet.navigateToQueueSource",
            mapOf("sourceMediaType" to sourceMediaType, "sourceMediaValue" to sourceMediaValue)
        )

        when (sourceMediaType) {
            MEDIA_TYPE_PLAYLIST -> navigator.navigate(LeafScreen.PlaylistDetail.buildRoute(sourceMediaValue.toLong()))
            MEDIA_TYPE_DOWNLOADS -> navigator.navigate(LeafScreen.Downloads().createRoute())
            MEDIA_TYPE_ARTIST -> navigator.navigate(LeafScreen.ArtistDetails.buildRoute(sourceMediaValue))
            MEDIA_TYPE_ALBUM -> navigator.navigate(LeafScreen.AlbumDetails.buildRoute(sourceMediaValue))
            MEDIA_TYPE_AUDIO_QUERY -> navigator.navigate(LeafScreen.Search.buildRoute(sourceMediaValue))
            else -> Unit
        }
    }

    fun onTitleClick() = viewModelScope.launch {
        val nowPlaying = playbackConnection.nowPlaying.value
        val query = nowPlaying.toAlbumSearchQuery()
        analytics.event("playbackSheet.onTitleClick", mapOf("query" to query))
        navigator.navigate(LeafScreen.Search.buildRoute(nowPlaying.toAlbumSearchQuery(), SoundspotSearchParams.BackendType.ALBUMS))
    }

    fun onArtistClick() = viewModelScope.launch {
        val nowPlaying = playbackConnection.nowPlaying.value
        val query = nowPlaying.toArtistSearchQuery()
        analytics.event("playbackSheet.onArtistClick", mapOf("query" to query))
        navigator.navigate(
            LeafScreen.Search.buildRoute(
                query,
                SoundspotSearchParams.BackendType.ARTISTS, SoundspotSearchParams.BackendType.ALBUMS
            )
        )
    }
}
