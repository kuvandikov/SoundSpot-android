/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.library.playlists

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kuvandikov.datmusic.domain.entities.Playlist
import com.kuvandikov.datmusic.ui.library.R
import com.kuvandikov.datmusic.ui.library.items.LibraryItemAction
import com.kuvandikov.datmusic.ui.library.items.LibraryItemRow
import com.kuvandikov.navigation.LocalNavigator
import com.kuvandikov.navigation.Navigator
import com.kuvandikov.navigation.screens.EditPlaylistScreen
import com.kuvandikov.navigation.screens.LeafScreen

@Composable
internal fun PlaylistRow(
    playlist: Playlist,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit,
    onDownload: () -> Unit,
    navigator: Navigator = LocalNavigator.current
) {
    LibraryItemRow(
        libraryItem = playlist,
        modifier = modifier,
        typeRes = R.string.playlist_title,
        onClick = {
            navigator.navigate(LeafScreen.PlaylistDetail.buildRoute(playlist.id))
        },
        imageData = playlist.artworkFile()
    ) {
        when (it) {
            is LibraryItemAction.Edit -> navigator.navigate(EditPlaylistScreen.buildRoute(playlist.id))
            is LibraryItemAction.Delete -> onDelete()
            is LibraryItemAction.Download -> onDownload()
        }
    }
}
