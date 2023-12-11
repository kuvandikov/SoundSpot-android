/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.album

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import com.kuvandikov.base.util.extensions.interpunctize
import com.kuvandikov.base.util.extensions.orNA
import com.kuvandikov.common.compose.LocalIsPreviewMode
import com.kuvandikov.common.compose.previews.CombinedPreview
import com.kuvandikov.common.compose.rememberFlowWithLifecycle
import com.kuvandikov.datmusic.data.SampleData
import com.kuvandikov.datmusic.domain.entities.Album
import com.kuvandikov.datmusic.playback.PlaybackConnection
import com.kuvandikov.datmusic.playback.models.MEDIA_ID_INDEX_SHUFFLED
import com.kuvandikov.datmusic.ui.components.ShuffleAdaptiveButton
import com.kuvandikov.datmusic.ui.detail.MediaDetail
import com.kuvandikov.datmusic.ui.playback.LocalPlaybackConnection
import com.kuvandikov.datmusic.ui.previews.PreviewDatmusicCore
import com.kuvandikov.domain.models.Loading
import com.kuvandikov.domain.models.Success
import com.kuvandikov.ui.components.CoverImage
import com.kuvandikov.ui.simpleClickable
import com.kuvandikov.ui.theme.AppTheme

@Composable
fun AlbumDetailRoute(isPreviewMode: Boolean = LocalIsPreviewMode.current) {
    when {
        isPreviewMode -> AlbumDetailPreview()
        else -> AlbumDetail()
    }
}

@Composable
private fun AlbumDetail(
    viewModel: AlbumDetailViewModel = hiltViewModel(),
) {
    val viewState by rememberFlowWithLifecycle(viewModel.state)
    AlbumDetail(
        viewState = viewState,
        onRefresh = viewModel::refresh,
        onArtistClick = viewModel::goToArtist,
    )
}

@Composable
private fun AlbumDetail(
    viewState: AlbumDetailViewState,
    onRefresh: () -> Unit,
    onArtistClick: () -> Unit,
    playbackConnection: PlaybackConnection = LocalPlaybackConnection.current,
) {
    val albumId = viewState.album?.id
    MediaDetail(
        viewState = viewState,
        titleRes = R.string.albums_detail_title,
        onFailRetry = onRefresh,
        onEmptyRetry = onRefresh,
        mediaDetailContent = AlbumDetailContent(viewState.album ?: Album()),
        headerCoverIcon = rememberVectorPainter(Icons.Default.Album),
        extraHeaderContent = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(AppTheme.specs.paddingSmall),
                    modifier = Modifier.weight(7f)
                ) {
                    AlbumHeaderSubtitle(
                        viewState = viewState,
                        onArtistClick = onArtistClick
                    )
                }
                ShuffleAdaptiveButton(
                    visible = !viewState.isLoading && !viewState.isEmpty,
                    modifier = Modifier.weight(1f),
                ) {
                    if (albumId != null)
                        playbackConnection.playAlbum(albumId, MEDIA_ID_INDEX_SHUFFLED)
                }
            }
        },
    )
}

@Composable
private fun AlbumHeaderSubtitle(viewState: AlbumDetailViewState, onArtistClick: () -> Unit) {
    val artist = viewState.album?.artists?.firstOrNull()
    val albumSubtitle = listOfNotNull(stringResource(R.string.albums_detail_title), viewState.album?.displayYear).interpunctize()
    val artistName = artist?.name.orNA()

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppTheme.specs.paddingSmall)) {
        CoverImage(artist?.photo(), shape = CircleShape, size = 20.dp)
        Text(
            artistName, style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.simpleClickable(onClick = onArtistClick)
        )
    }
    Text(albumSubtitle, style = MaterialTheme.typography.bodySmall)
}

@CombinedPreview
@Composable
private fun AlbumDetailPreview() = PreviewDatmusicCore {
    var viewState by remember { mutableStateOf(AlbumDetailViewState.Empty) }
    LaunchedEffect(Unit) {
        delay(1000)
        val artist = SampleData.artist()
        val album = SampleData.album(mainArtist = artist)
        viewState = viewState.copy(album = album, albumDetails = Loading())
        delay(1000)
        viewState = viewState.copy(
            albumDetails = Success(
                SampleData.list {
                    audio().copy(artist = artist.name)
                }
            )
        )
    }
    AlbumDetail(
        viewState = viewState,
        onRefresh = {},
        onArtistClick = {},
    )
}
