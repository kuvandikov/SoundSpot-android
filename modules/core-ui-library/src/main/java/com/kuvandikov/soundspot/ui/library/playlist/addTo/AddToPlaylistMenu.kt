/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.library.playlist.addTo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kuvandikov.common.compose.LocalIsPreviewMode
import com.kuvandikov.common.compose.rememberFlowWithLifecycle
import com.kuvandikov.soundspot.domain.entities.Audio
import com.kuvandikov.soundspot.domain.entities.Audios
import com.kuvandikov.soundspot.domain.entities.Playlist
import com.kuvandikov.soundspot.ui.coreLibrary.R
import com.kuvandikov.soundspot.ui.library.playlist.addTo.NewPlaylistItem.isNewPlaylistItem
import com.kuvandikov.soundspot.ui.library.playlist.addTo.NewPlaylistItem.withNewPlaylistItem
import com.kuvandikov.navigation.activityHiltViewModel
import com.kuvandikov.ui.theme.AppTheme

@Composable
fun AddToPlaylistMenu(
    audio: Audio,
    visible: Boolean,
    onVisibleChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    AddToPlaylistMenu(
        audios = listOf(audio),
        visible = visible,
        onVisibleChange = onVisibleChange,
        modifier = modifier
    )
}

@Composable
fun AddToPlaylistMenu(
    audios: Audios,
    visible: Boolean,
    onVisibleChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    AddToPlaylistDropdownMenu(
        audios = audios,
        visible = visible,
        onVisibleChange = onVisibleChange,
        modifier = modifier,
    )
}

@Composable
internal fun AddToPlaylistDropdownMenu(
    audios: Audios,
    visible: Boolean,
    onVisibleChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isPreviewMode: Boolean = LocalIsPreviewMode.current,
    viewModel: AddToPlaylistViewModel = when {
        isPreviewMode -> PreviewAddToPlaylistViewModel
        else -> activityHiltViewModel<AddToPlaylistViewModelImpl>()
    },
) {
    val playlists by rememberFlowWithLifecycle(viewModel.playlists)

    if (visible)
        AddToPlaylistDropdownMenu(
            expanded = visible,
            onExpandedChange = onVisibleChange,
            multiple = audios.size > 1,
            playlists = playlists.withNewPlaylistItem(),
            onPlaylistSelect = { viewModel.addTo(playlist = it, audios.map { audio -> audio.id }) },
            modifier = modifier,
        )
}

@Composable
private fun AddToPlaylistDropdownMenu(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    multiple: Boolean = false,
    playlists: List<Playlist> = emptyList(),
    onPlaylistSelect: (Playlist) -> Unit = {}
) {
    Box {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Max)
                .heightIn(max = 400.dp)
                .align(Alignment.Center)
        ) {
            Text(
                if (multiple) stringResource(R.string.playlist_addTo_multiple) else stringResource(R.string.playlist_addTo),
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.secondary),
                modifier = Modifier.padding(AppTheme.specs.inputPaddings)
            )
            playlists.forEach { item ->
                val label = item.name
                DropdownMenuItem(
                    onClick = {
                        onExpandedChange(false)
                        onPlaylistSelect(item)
                    },
                    text = {
                        Text(
                            text = label,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = if (item.isNewPlaylistItem()) FontWeight.Bold else null,
                            maxLines = 1,
                        )
                    }
                )
            }
        }
    }
}
