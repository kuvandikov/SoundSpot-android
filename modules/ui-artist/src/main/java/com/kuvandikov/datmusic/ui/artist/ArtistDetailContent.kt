/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.artist

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.kuvandikov.datmusic.domain.entities.Album
import com.kuvandikov.datmusic.domain.entities.Artist
import com.kuvandikov.datmusic.domain.entities.Audio
import com.kuvandikov.datmusic.ui.albums.AlbumColumn
import com.kuvandikov.datmusic.ui.audios.AudioRow
import com.kuvandikov.datmusic.ui.detail.MediaDetailContent
import com.kuvandikov.datmusic.ui.playback.LocalPlaybackConnection
import com.kuvandikov.domain.models.Async
import com.kuvandikov.domain.models.Loading
import com.kuvandikov.domain.models.Success
import com.kuvandikov.navigation.LocalNavigator
import com.kuvandikov.navigation.screens.LeafScreen
import com.kuvandikov.ui.theme.AppTheme

internal class ArtistDetailContent : MediaDetailContent<Artist>() {
    override fun invoke(list: LazyListScope, details: Async<Artist>, detailsLoading: Boolean): Boolean {
        val artistAlbums = when (details) {
            is Success -> details().albums
            is Loading -> (1..5).map { Album.withLoadingYear() }
            else -> emptyList()
        }
        val artistAudios = when (details) {
            is Success -> details().audios
            is Loading -> (1..5).map { Audio() }
            else -> emptyList()
        }

        if (artistAlbums.isNotEmpty()) {
            list.item {
                Text(
                    stringResource(R.string.search_albums),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppTheme.specs.inputPaddings)
                )
            }

            list.item {
                LazyRow(Modifier.fillMaxWidth()) {
                    items(artistAlbums) { album ->
                        val navigator = LocalNavigator.current
                        AlbumColumn(
                            album = album,
                            isPlaceholder = detailsLoading,
                        ) {
                            navigator.navigate(LeafScreen.AlbumDetails.buildRoute(album))
                        }
                    }
                }
            }
        }

        if (artistAudios.isNotEmpty()) {
            list.item {
                Text(
                    stringResource(R.string.search_audios),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppTheme.specs.inputPaddings)
                )
            }

            list.itemsIndexed(artistAudios, key = { i, a -> a.id + i }) { index, audio ->
                val playbackConnection = LocalPlaybackConnection.current
                AudioRow(
                    audio = audio,
                    isPlaceholder = detailsLoading,
                    onPlayAudio = {
                        if (details is Success)
                            playbackConnection.playArtist(details().id, index)
                    }
                )
            }
        }
        return artistAlbums.isEmpty() && artistAudios.isEmpty()
    }
}
