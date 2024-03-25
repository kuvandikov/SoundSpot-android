/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.album

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.kuvandikov.base.util.extensions.stateInDefault
import com.kuvandikov.soundspot.data.SoundspotAlbumParams
import com.kuvandikov.soundspot.data.SoundspotArtistParams
import com.kuvandikov.soundspot.data.SoundspotSearchParams
import com.kuvandikov.soundspot.data.interactors.album.GetAlbumDetails
import com.kuvandikov.soundspot.data.observers.album.ObserveAlbum
import com.kuvandikov.soundspot.data.observers.album.ObserveAlbumDetails
import com.kuvandikov.soundspot.data.observers.artist.ObserveArtist
import com.kuvandikov.navigation.Navigator
import com.kuvandikov.navigation.screens.ALBUM_ACCESS_KEY
import com.kuvandikov.navigation.screens.ALBUM_ID_KEY
import com.kuvandikov.navigation.screens.ALBUM_OWNER_ID_KEY
import com.kuvandikov.navigation.screens.LeafScreen

@HiltViewModel
internal class AlbumDetailViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val albumObserver: ObserveAlbum,
    private val albumDetails: ObserveAlbumDetails,
    private val observeArtist: ObserveArtist,
    private val navigator: Navigator,
) : ViewModel() {

    private val albumParams = SoundspotAlbumParams(
        requireNotNull(handle.get<String>(ALBUM_ID_KEY)),
        requireNotNull(handle.get<Long>(ALBUM_OWNER_ID_KEY)),
        requireNotNull(handle.get<String>(ALBUM_ACCESS_KEY))
    )

    val state = combine(albumObserver.flow, albumDetails.asyncFlow, ::AlbumDetailViewState)
        .stateInDefault(viewModelScope, AlbumDetailViewState.Empty)

    init {
        load()
    }

    private fun load(forceRefresh: Boolean = false) {
        albumObserver(albumParams)
        albumDetails(GetAlbumDetails.Params(albumParams, forceRefresh))
    }

    fun refresh() = load(true)

    /**
     * Navigate to artist detail screen if album's main artist is in the database already, matched by id.
     * Otherwise navigate to search screen with artist name as query.
     */
    fun goToArtist() = viewModelScope.launch {
        val album = state.first().album
        if (album != null) {
            val artist = album.artists.firstOrNull()
            if (artist != null) {
                val id = artist.id
                val name = artist.name
                observeArtist(SoundspotArtistParams(id))
                val route = when (observeArtist.getOrNull() != null) {
                    true -> LeafScreen.ArtistDetails.buildRoute(id)
                    else -> LeafScreen.Search.buildRoute(
                        name,
                        SoundspotSearchParams.BackendType.ARTISTS,
                        SoundspotSearchParams.BackendType.ALBUMS
                    )
                }
                navigator.navigate(route)
            }
        }
    }
}
