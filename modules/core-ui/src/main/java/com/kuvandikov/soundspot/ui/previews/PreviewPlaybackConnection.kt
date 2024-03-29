/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.previews

import android.content.Context
import android.graphics.BitmapFactory
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.kuvandikov.soundspot.data.SampleData
import com.kuvandikov.soundspot.domain.entities.AlbumId
import com.kuvandikov.soundspot.domain.entities.ArtistId
import com.kuvandikov.soundspot.domain.entities.Audio
import com.kuvandikov.soundspot.domain.entities.AudioId
import com.kuvandikov.soundspot.domain.entities.PlaylistId
import com.kuvandikov.soundspot.playback.PlaybackConnection
import com.kuvandikov.soundspot.playback.models.PlaybackModeState
import com.kuvandikov.soundspot.playback.models.PlaybackProgressState
import com.kuvandikov.soundspot.playback.models.PlaybackQueue
import com.kuvandikov.soundspot.playback.models.QueueTitle
import com.kuvandikov.soundspot.ui.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

lateinit var previewPlaybackConnectionInstance: PlaybackConnection

@Composable
internal fun previewPlaybackConnection(): PlaybackConnection {
    if (!::previewPlaybackConnectionInstance.isInitialized) {
        previewPlaybackConnectionInstance = PreviewPlaybackConnection(LocalContext.current)
    }
    return previewPlaybackConnectionInstance
}

private class PreviewPlaybackConnection constructor(context: Context) : PlaybackConnection {
    private val previewPlaybackState = MutableStateFlow(
        PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_PLAYING, 4000, 1.0f)
            .build()
    )
    private val previewPlaybackNowPlaying = MutableStateFlow(
        MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "Artist")
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "Title")
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "Album")
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "id")
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 40000)
            .putBitmap(
                MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                BitmapFactory.decodeResource(context.resources, R.drawable.preview_artwork)
            )
            .build()
    )

    private val previewPlaybackQueue = MutableStateFlow(
        PlaybackQueue(audios = SampleData.list(20) { audio() })
            .let { it.copy(ids = it.audios.map { it.id }) }
    )

    private val previewPlaybackProgress = MutableStateFlow(
        PlaybackProgressState(
            total = 148000,
            lastPosition = 23255,
            elapsed = 12134,
            buffered = 52000,
        )
    )

    override val playbackState: StateFlow<PlaybackStateCompat> = previewPlaybackState
    override val nowPlaying: StateFlow<MediaMetadataCompat> = previewPlaybackNowPlaying
    override val playbackQueue: StateFlow<PlaybackQueue> = previewPlaybackQueue
    override val playbackProgress: StateFlow<PlaybackProgressState> = previewPlaybackProgress

    override val isConnected: StateFlow<Boolean> = MutableStateFlow(true)
    override val nowPlayingAudio: StateFlow<PlaybackQueue.NowPlayingAudio?> = MutableStateFlow(null)
    override val playbackMode: StateFlow<PlaybackModeState> = MutableStateFlow(PlaybackModeState())
    override val mediaController: MediaControllerCompat? = null
    override val transportControls: MediaControllerCompat.TransportControls? = null

    override fun playAudio(audio: Audio, title: QueueTitle) {}
    override fun playAudios(audios: List<Audio>, index: Int, title: QueueTitle) {}
    override fun playArtist(artistId: ArtistId, index: Int) {}
    override fun playPlaylist(playlistId: PlaylistId, index: Int, queue: List<AudioId>) {}
    override fun playAlbum(albumId: AlbumId, index: Int) {}
    override fun playFromDownloads(index: Int, queue: List<AudioId>) {}
    override fun playWithQuery(query: String, audioId: String) {}
    override fun swapQueue(from: Int, to: Int) {}
    override fun removeById(id: String) {}

    override fun playNextAudio(audio: Audio) {
        previewPlaybackQueue.value = previewPlaybackQueue.value.copy(
            audios = previewPlaybackQueue.value.audios + audio,
            ids = previewPlaybackQueue.value.ids + audio.id
        )
    }

    override fun removeByPosition(position: Int) {
        previewPlaybackQueue.value = previewPlaybackQueue.value.copy(
            audios = previewPlaybackQueue.value.audios.toMutableList().apply { removeAt(position) },
            ids = previewPlaybackQueue.value.ids.toMutableList().apply { removeAt(position) },
        )
    }
}
