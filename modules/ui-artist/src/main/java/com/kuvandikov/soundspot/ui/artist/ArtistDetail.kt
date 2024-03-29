/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.artist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import com.kuvandikov.common.compose.LocalIsPreviewMode
import com.kuvandikov.common.compose.previews.CombinedPreview
import com.kuvandikov.common.compose.rememberFlowWithLifecycle
import com.kuvandikov.soundspot.data.SampleData
import com.kuvandikov.soundspot.playback.PlaybackConnection
import com.kuvandikov.soundspot.playback.models.MEDIA_ID_INDEX_SHUFFLED
import com.kuvandikov.soundspot.ui.components.ShuffleAdaptiveButton
import com.kuvandikov.soundspot.ui.detail.MediaDetail
import com.kuvandikov.soundspot.ui.playback.LocalPlaybackConnection
import com.kuvandikov.soundspot.ui.previews.PreviewSoundspotCore
import com.kuvandikov.domain.models.Loading
import com.kuvandikov.domain.models.Success

@Composable
fun ArtistDetailRoute(isPreviewMode: Boolean = LocalIsPreviewMode.current) {
    when {
        isPreviewMode -> ArtistDetailPreview()
        else -> ArtistDetail()
    }
}

@Composable
private fun ArtistDetail(viewModel: ArtistDetailViewModel = hiltViewModel()) {
    val viewState by rememberFlowWithLifecycle(viewModel.state)
    ArtistDetail(
        viewState = viewState,
        onRefresh = viewModel::refresh,
    )
}

@Composable
private fun ArtistDetail(
    viewState: ArtistDetailViewState,
    onRefresh: () -> Unit,
    playbackConnection: PlaybackConnection = LocalPlaybackConnection.current,
) {
    val artistId = viewState.artist?.id
    MediaDetail(
        viewState = viewState,
        titleRes = R.string.artists_detail_title,
        onFailRetry = onRefresh,
        onEmptyRetry = onRefresh,
        mediaDetailContent = ArtistDetailContent(),
        headerCoverIcon = rememberVectorPainter(Icons.Default.Person),
        extraHeaderContent = {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                ShuffleAdaptiveButton(visible = !viewState.isLoading && !viewState.isEmpty) {
                    if (artistId != null)
                        playbackConnection.playArtist(artistId, MEDIA_ID_INDEX_SHUFFLED)
                }
            }
        },
    )
}

@CombinedPreview
@Composable
private fun ArtistDetailPreview() = PreviewSoundspotCore {
    var viewState by remember { mutableStateOf(ArtistDetailViewState.Empty) }
    LaunchedEffect(Unit) {
        delay(1000)
        val artist = SampleData.artist()
        viewState = viewState.copy(artist = artist, artistDetails = Loading())
        delay(1000)
        viewState = viewState.copy(
            artistDetails = Success(
                artist.copy(
                    audios = SampleData.list {
                        audio()
                    }
                )
            )
        )
    }
    ArtistDetail(
        viewState = viewState,
        onRefresh = {},
    )
}
