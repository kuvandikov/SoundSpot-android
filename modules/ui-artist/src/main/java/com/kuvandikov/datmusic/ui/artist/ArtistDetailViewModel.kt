/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.artist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import com.kuvandikov.base.util.extensions.stateInDefault
import com.kuvandikov.datmusic.data.DatmusicArtistParams
import com.kuvandikov.datmusic.data.interactors.artist.GetArtistDetails
import com.kuvandikov.datmusic.data.observers.artist.ObserveArtist
import com.kuvandikov.datmusic.data.observers.artist.ObserveArtistDetails
import com.kuvandikov.navigation.screens.ARTIST_ID_KEY

@HiltViewModel
internal class ArtistDetailViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val artist: ObserveArtist,
    private val artistDetails: ObserveArtistDetails
) : ViewModel() {

    private val artistParams = DatmusicArtistParams(handle.get<String>(ARTIST_ID_KEY)!!)

    val state = combine(artist.flow, artistDetails.asyncFlow, ::ArtistDetailViewState)
        .stateInDefault(viewModelScope, ArtistDetailViewState.Empty)

    init {
        load()
    }

    private fun load(forceRefresh: Boolean = false) {
        artist(artistParams)
        artistDetails(GetArtistDetails.Params(artistParams, forceRefresh))
    }

    fun refresh() = load(true)
}
