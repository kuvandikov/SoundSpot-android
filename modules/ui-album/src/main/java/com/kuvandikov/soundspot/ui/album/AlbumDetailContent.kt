/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.album

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import com.kuvandikov.soundspot.domain.entities.Album
import com.kuvandikov.soundspot.domain.entities.Audio
import com.kuvandikov.soundspot.domain.entities.Audios
import com.kuvandikov.soundspot.ui.audios.AudioRow
import com.kuvandikov.soundspot.ui.detail.MediaDetailContent
import com.kuvandikov.soundspot.ui.playback.LocalPlaybackConnection
import com.kuvandikov.domain.models.Async
import com.kuvandikov.domain.models.Loading
import com.kuvandikov.domain.models.Success

internal class AlbumDetailContent(val album: Album) : MediaDetailContent<Audios>() {

    override fun invoke(list: LazyListScope, details: Async<Audios>, detailsLoading: Boolean): Boolean {
        val albumAudios = when (details) {
            is Success -> details()
            is Loading -> (1..album.songCount).map { Audio() }
            else -> emptyList()
        }

        if (albumAudios.isNotEmpty()) {
            list.itemsIndexed(albumAudios, key = { i, a -> a.id + i }) { index, audio ->
                val playbackConnection = LocalPlaybackConnection.current
                AudioRow(
                    audio = audio,
                    isPlaceholder = detailsLoading,
                    includeCover = false,
                    onPlayAudio = {
                        if (details is Success)
                            playbackConnection.playAlbum(album.id, index)
                    }
                )
            }
        }
        return albumAudios.isEmpty()
    }
}
