/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.library.playlists.edit

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import com.kuvandikov.base.util.Analytics
import com.kuvandikov.base.util.extensions.orBlank
import com.kuvandikov.base.util.extensions.stateInDefault
import com.kuvandikov.base.util.extensions.swap
import com.kuvandikov.soundspot.data.interactors.playlist.ClearPlaylistArtwork
import com.kuvandikov.soundspot.data.interactors.playlist.DeletePlaylist
import com.kuvandikov.soundspot.data.interactors.playlist.RemovePlaylistItems
import com.kuvandikov.soundspot.data.interactors.playlist.SetCustomPlaylistArtwork
import com.kuvandikov.soundspot.data.interactors.playlist.UpdatePlaylist
import com.kuvandikov.soundspot.data.interactors.playlist.UpdatePlaylistItems
import com.kuvandikov.soundspot.data.observers.playlist.ObservePlaylist
import com.kuvandikov.soundspot.data.observers.playlist.ObservePlaylistDetails
import com.kuvandikov.soundspot.domain.entities.PlaylistAudioId
import com.kuvandikov.soundspot.domain.entities.PlaylistId
import com.kuvandikov.soundspot.domain.entities.PlaylistItem
import com.kuvandikov.i18n.ValidationError
import com.kuvandikov.i18n.asValidationError
import com.kuvandikov.navigation.Navigator
import com.kuvandikov.navigation.screens.PLAYLIST_ID_KEY

@HiltViewModel
internal class EditPlaylistViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val observePlaylist: ObservePlaylist,
    private val observePlaylistDetails: ObservePlaylistDetails,
    private val updatePlaylist: UpdatePlaylist,
    private val deletePlaylist: DeletePlaylist,
    private val reorderPlaylist: UpdatePlaylistItems,
    private val removePlaylistItems: RemovePlaylistItems,
    private val setCustomPlaylistArtwork: SetCustomPlaylistArtwork,
    private val clearPlaylistArtwork: ClearPlaylistArtwork,
    private val analytics: Analytics,
    private val navigator: Navigator,
) : ViewModel() {

    private val playlistId = requireNotNull(handle.get<PlaylistId>(PLAYLIST_ID_KEY))
    private val playlistDetailParams = ObservePlaylistDetails.Params(playlistId)

    private val nameState = MutableStateFlow("")
    private val nameErrorState = MutableStateFlow<ValidationError?>(null)
    private val removedPlaylistItems = MutableStateFlow<Set<PlaylistItem>>(setOf())
    private val lastRemovedItemState = MutableStateFlow<RemovedFromPlaylist?>(null)

    val state = combine(
        nameState, nameErrorState,
        observePlaylist.flow, lastRemovedItemState,
        transform = ::EditPlaylistViewState
    ).stateInDefault(viewModelScope, EditPlaylistViewState.Empty)

    val playlistItemsState = mutableStateListOf<PlaylistItem>()

    init {
        observePlaylist(playlistId)
        observePlaylistDetails(playlistDetailParams)

        viewModelScope.launch {
            val playlist = observePlaylist.get()
            nameState.value = playlist.name

            observePlaylistDetails.flow.collectLatest {
                playlistItemsState.clear()
                playlistItemsState.addAll(it)
            }
        }
    }

    fun setPlaylistName(value: String) {
        nameState.value = value
        nameErrorState.value = null
    }

    fun removePlaylistItem(id: PlaylistAudioId) {
        var removedItem: PlaylistItem
        var removedIndex: Int
        playlistItemsState.also {
            removedIndex = it.indexOfFirst { item -> item.playlistAudio.id == id }
            removedItem = it.removeAt(removedIndex)
        }
        removedPlaylistItems.value = removedPlaylistItems.value.toMutableSet().also {
            it.add(removedItem)
        }

        lastRemovedItemState.value = RemovedFromPlaylist(removedItem, removedIndex)

        // clear last removed state with delay unless list is empty now
        if (playlistItemsState.isNotEmpty())
            clearLastRemovedPlaylistItem(delay = true)
        analytics.event("playlists.edit.remove", mapOf("playlistId" to playlistId, "audioId" to removedItem.audio.id))
    }

    fun undoLastRemovedPlaylistItem() {
        val removedItemMessage = lastRemovedItemState.value ?: error("Can't undo, no removed item")
        val removedItem = removedItemMessage.playlistItem
        val removedIndex = removedItemMessage.removedIndex

        playlistItemsState.add(removedIndex, removedItem)
        removedPlaylistItems.value = removedPlaylistItems.value.toMutableSet().also {
            it.remove(removedItem)
        }
        clearLastRemovedPlaylistItem()
        analytics.event("playlists.edit.undoRemove", mapOf("playlistId" to playlistId, "audioId" to removedItem.audio.id))
    }

    private var delayedClearLastRemovedJob: Job? = null

    fun clearLastRemovedPlaylistItem(delay: Boolean = false) {
        if (delay) {
            delayedClearLastRemovedJob?.cancel()
            delayedClearLastRemovedJob = viewModelScope.launch {
                delay(RemovedFromPlaylist.SNACKBAR_DURATION_MILLIS)
                clearLastRemovedPlaylistItem(delay = false)
            }
        } else lastRemovedItemState.value = null
    }

    fun movePlaylistItem(from: Int, to: Int) {
        playlistItemsState.swap(from, to)
    }

    fun shufflePlaylist() {
        analytics.event("playlists.edit.shuffle", mapOf("playlistId" to playlistId))
        playlistItemsState.shuffle()
    }

    fun deletePlaylist() = viewModelScope.launch {
        analytics.event("playlists.edit.delete", mapOf("playlistId" to playlistId))
        deletePlaylist(playlistId).collectLatest {
            navigator.goBack()
        }
    }

    fun setPlaylistArtwork(uri: Uri) = viewModelScope.launch {
        analytics.event("playlists.edit.setArtwork", mapOf("playlistId" to playlistId))
        setCustomPlaylistArtwork.execute(SetCustomPlaylistArtwork.Params(playlistId, uri))
    }

    fun clearPlaylistArtwork() = viewModelScope.launch {
        analytics.event("playlists.edit.clearArtwork", mapOf("playlistId" to playlistId))
        clearPlaylistArtwork.execute(playlistId)
    }

    fun save() {
        analytics.event("playlists.edit.save", mapOf("playlistId" to playlistId))
        viewModelScope.launch {
            removePlaylistItems.execute(removedPlaylistItems.value.map { it.playlistAudio.id })

            val repositionedItems = playlistItemsState.mapIndexed { index, it ->
                it.copy(playlistAudio = it.playlistAudio.copy(position = index))
            }
            reorderPlaylist.execute(repositionedItems)

            val playlist = observePlaylist.get().copy(name = nameState.value.orBlank())
            updatePlaylist(playlist).catch {
                nameErrorState.value = it.asValidationError()
            }.collectLatest {
                navigator.goBack()
            }
        }
    }
}
