/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.library

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.kuvandikov.common.compose.LocalIsPreviewMode
import com.kuvandikov.common.compose.previews.CombinedPreview
import com.kuvandikov.common.compose.rememberFlowWithLifecycle
import com.kuvandikov.datmusic.data.SampleData
import com.kuvandikov.datmusic.domain.entities.Album
import com.kuvandikov.datmusic.domain.entities.LibraryItems
import com.kuvandikov.datmusic.domain.entities.Playlist
import com.kuvandikov.datmusic.domain.entities.PlaylistId
import com.kuvandikov.datmusic.ui.library.items.LibraryItemRow
import com.kuvandikov.datmusic.ui.library.playlists.PlaylistRow
import com.kuvandikov.datmusic.ui.previews.PreviewDatmusicCore
import com.kuvandikov.domain.models.Success
import com.kuvandikov.navigation.LocalNavigator
import com.kuvandikov.navigation.Navigator
import com.kuvandikov.navigation.screens.LeafScreen
import com.kuvandikov.ui.ProvideScaffoldPadding
import com.kuvandikov.ui.components.AppTopBar
import com.kuvandikov.ui.components.EmptyErrorBox
import com.kuvandikov.ui.components.FullScreenLoading
import com.kuvandikov.ui.components.IconButton
import com.kuvandikov.ui.scaffoldPadding
import com.kuvandikov.ui.theme.AppTheme

@Composable
fun LibraryRoute(isPreviewMode: Boolean = LocalIsPreviewMode.current) {
    when {
        isPreviewMode -> LibraryPreview()
        else -> Library()
    }
}

@Composable
private fun Library(viewModel: LibraryViewModel = hiltViewModel()) {
    val viewState by rememberFlowWithLifecycle(viewModel.state)
    Library(
        viewState = viewState,
        onDeletePlaylist = viewModel::onDeletePlaylist,
        onDownloadPlaylist = viewModel::onDownloadPlaylist,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Library(
    viewState: LibraryViewState,
    onDeletePlaylist: (PlaylistId) -> Unit,
    onDownloadPlaylist: (PlaylistId) -> Unit,
    listState: LazyListState = rememberLazyListState(),
    navigator: Navigator = LocalNavigator.current
) {
    Scaffold(
        topBar = {
            LibraryTopBar(
                onCreatePlaylist = {
                    navigator.navigate(LeafScreen.CreatePlaylist().createRoute())
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddings ->
        ProvideScaffoldPadding(paddings) {
            when (val items = viewState.items) {
                is Success -> LazyColumn(contentPadding = scaffoldPadding(), state = listState) {
                    libraryList(
                        items = items(),
                        onDelete = onDeletePlaylist,
                        onDownload = onDownloadPlaylist,
                    )
                }
                else -> FullScreenLoading()
            }
        }
    }
}

@Composable
private fun LibraryTopBar(onCreatePlaylist: () -> Unit) {
    AppTopBar(
        title = stringResource(R.string.library_title),
        actions = {
            IconButton(
                onClick = onCreatePlaylist,
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.playlist_create),
                    modifier = Modifier.size(AppTheme.specs.iconSize)
                )
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.libraryList(
    items: LibraryItems = listOf(),
    onDelete: (PlaylistId) -> Unit,
    onDownload: (PlaylistId) -> Unit,
) {
    val downloadsEmpty = items.isEmpty()
    if (downloadsEmpty) {
        item {
            EmptyErrorBox(
                message = stringResource(R.string.library_empty),
                retryVisible = false,
                modifier = Modifier.fillParentMaxHeight()
            )
        }
    }

    items(items, key = { it.getIdentifier() }) {
        val modifier = Modifier.animateItemPlacement()
        when (it) {
            is Playlist -> PlaylistRow(
                playlist = it,
                onDelete = { onDelete(it.id) },
                onDownload = { onDownload(it.id) },
                modifier = modifier
            )
            is Album -> LibraryItemRow(libraryItem = it, typeRes = R.string.albums_detail_title, modifier)
            else -> LibraryItemRow(libraryItem = it, typeRes = R.string.unknown, modifier)
        }
    }
}

@CombinedPreview
@Composable
private fun LibraryPreview() = PreviewDatmusicCore {
    val playlists = remember(Unit) { SampleData.list { playlist() } }
    val viewState by remember { mutableStateOf(LibraryViewState.Empty.copy(items = Success(playlists))) }
    Library(
        viewState = viewState,
        onDeletePlaylist = {},
        onDownloadPlaylist = {},
    )
}
