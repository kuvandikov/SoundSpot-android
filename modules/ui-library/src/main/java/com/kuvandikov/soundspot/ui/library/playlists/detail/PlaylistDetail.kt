/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.library.playlists.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import com.kuvandikov.common.compose.LocalIsPreviewMode
import com.kuvandikov.common.compose.previews.CombinedPreview
import com.kuvandikov.common.compose.rememberFlowWithLifecycle
import com.kuvandikov.soundspot.data.SampleData
import com.kuvandikov.soundspot.data.observers.playlist.PlaylistItemSortOption
import com.kuvandikov.soundspot.domain.entities.PlaylistItem
import com.kuvandikov.soundspot.playback.PlaybackConnection
import com.kuvandikov.soundspot.playback.models.MEDIA_ID_INDEX_SHUFFLED
import com.kuvandikov.soundspot.ui.components.ShuffleAdaptiveButton
import com.kuvandikov.soundspot.ui.detail.MediaDetail
import com.kuvandikov.soundspot.ui.library.R
import com.kuvandikov.soundspot.ui.playback.LocalPlaybackConnection
import com.kuvandikov.soundspot.ui.previews.PreviewSoundspotCore
import com.kuvandikov.soundspot.ui.utils.AudiosCountDurationTextCreator.localize
import com.kuvandikov.domain.models.Loading
import com.kuvandikov.domain.models.Success
import com.kuvandikov.navigation.LocalNavigator
import com.kuvandikov.navigation.Navigator
import com.kuvandikov.navigation.screens.EditPlaylistScreen

@Composable
fun PlaylistDetailRoute(isPreviewMode: Boolean = LocalIsPreviewMode.current) {
    when {
        isPreviewMode -> PlaylistDetailPreview()
        else -> PlaylistDetail()
    }
}

@Composable
private fun PlaylistDetail(viewModel: PlaylistDetailViewModel = hiltViewModel()) {
    val viewState by rememberFlowWithLifecycle(viewModel.state)
    PlaylistDetail(
        viewState = viewState,
        onRefresh = viewModel::refresh,
        onAddSongs = viewModel::addSongs,
        onRemovePlaylistItem = viewModel::onRemovePlaylistItem,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onSortOptionSelect = viewModel::onSortOptionSelect,
        onClearFilter = viewModel::onClearFilter,
        onPlayPlaylistItem = viewModel::onPlayPlaylistItem,
    )
}

@Composable
private fun PlaylistDetail(
    viewState: PlaylistDetailViewState,
    onRefresh: () -> Unit,
    onAddSongs: () -> Unit,
    onRemovePlaylistItem: (PlaylistItem) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSortOptionSelect: (PlaylistItemSortOption) -> Unit,
    onClearFilter: () -> Unit,
    onPlayPlaylistItem: (PlaylistItem) -> Unit,
    navigator: Navigator = LocalNavigator.current,
    playbackConnection: PlaybackConnection = LocalPlaybackConnection.current,
) {
    var filterVisible by rememberSaveable { mutableStateOf(false) }
    val playlistId = viewState.playlist?.id
    MediaDetail(
        viewState = viewState,
        scrollbarsEnabled = viewState.isLoaded && !viewState.isEmpty,
        titleRes = R.string.playlist_title,
        onFailRetry = onRefresh,
        onEmptyRetry = onAddSongs,
        onTitleClick = {
            if (playlistId != null)
                navigator.navigate(EditPlaylistScreen.buildRoute(playlistId))
        },
        isHeaderVisible = !filterVisible,
        mediaDetailContent = PlaylistDetailContent(
            onPlayAudio = onPlayPlaylistItem,
            onRemoveFromPlaylist = onRemovePlaylistItem,
        ),
        mediaDetailTopBar = PlaylistDetailTopBar(
            filterVisible = filterVisible,
            setFilterVisible = { filterVisible = it },
            searchQuery = viewState.params.query,
            hasSortingOption = viewState.params.hasSortingOption,
            sortOptions = viewState.params.sortOptions,
            sortOption = viewState.params.sortOption,
            onSearchQueryChange = onSearchQueryChange,
            onSortOptionSelect = onSortOptionSelect,
            onClearFilter = onClearFilter,
        ),
        mediaDetailEmpty = PlaylistDetailEmpty(),
        mediaDetailFail = PlaylistDetailFail(),
        extraHeaderContent = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                PlaylistHeaderSubtitle(viewState)
                ShuffleAdaptiveButton(
                    visible = !viewState.isLoading && !viewState.isEmpty,
                    playbackConnection = playbackConnection,
                ) {
                    if (playlistId != null)
                        playbackConnection.playPlaylist(playlistId, MEDIA_ID_INDEX_SHUFFLED)
                }
            }
        },
    )
}

@Composable
private fun PlaylistHeaderSubtitle(viewState: PlaylistDetailViewState) {
    if (!viewState.isEmpty) {
        val resources = LocalContext.current.resources
        val countDuration = viewState.audiosCountDuration
        if (countDuration != null)
            Text(countDuration.localize(resources), style = MaterialTheme.typography.titleSmall)
        else Spacer(Modifier)
    } else Spacer(Modifier)
}

@CombinedPreview
@Composable
private fun PlaylistDetailPreview() = PreviewSoundspotCore {
    val playlistItems = remember(Unit) { SampleData.list { playlistItem() } }
    var viewState by remember { mutableStateOf(PlaylistDetailViewState.Empty) }
    LaunchedEffect(Unit) {
        delay(1000)
        val playlist = SampleData.playlist()
        viewState = viewState.copy(playlist = playlist, playlistDetails = Loading())
        delay(1000)
        viewState = viewState.copy(playlistDetails = Success(playlistItems))
    }
    PlaylistDetail(
        viewState = viewState,
        onRefresh = {},
        onAddSongs = {},
        onRemovePlaylistItem = {
            viewState = viewState.copy(playlistDetails = Success(playlistItems - it))
        },
        onSearchQueryChange = { query ->
            viewState = viewState.copy(
                params = viewState.params.copy(query = query),
                playlistDetails = Success(playlistItems.filter { query in it.toString() })
            )
        },
        onSortOptionSelect = {
            val sortedPlaylistItems = it.comparator?.run { playlistItems.sortedWith(this) } ?: playlistItems
            viewState = viewState.copy(
                params = viewState.params.copy(sortOption = it),
                playlistDetails = Success(sortedPlaylistItems)
            )
        },
        onClearFilter = {},
        onPlayPlaylistItem = {},
    )
}
