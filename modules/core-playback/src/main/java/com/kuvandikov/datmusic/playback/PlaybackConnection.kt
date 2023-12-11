/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.playback

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import com.kuvandikov.base.ui.SnackbarManager
import com.kuvandikov.base.util.extensions.flowInterval
import com.kuvandikov.base.util.extensions.stateInDefault
import com.kuvandikov.datmusic.data.repos.audio.AudiosRepo
import com.kuvandikov.datmusic.domain.entities.AlbumId
import com.kuvandikov.datmusic.domain.entities.ArtistId
import com.kuvandikov.datmusic.domain.entities.Audio
import com.kuvandikov.datmusic.domain.entities.AudioId
import com.kuvandikov.datmusic.domain.entities.PlaylistId
import com.kuvandikov.datmusic.downloader.Downloader
import com.kuvandikov.datmusic.playback.models.MEDIA_TYPE_ALBUM
import com.kuvandikov.datmusic.playback.models.MEDIA_TYPE_ARTIST
import com.kuvandikov.datmusic.playback.models.MEDIA_TYPE_AUDIO
import com.kuvandikov.datmusic.playback.models.MEDIA_TYPE_AUDIO_FLACS_QUERY
import com.kuvandikov.datmusic.playback.models.MEDIA_TYPE_AUDIO_MINERVA_QUERY
import com.kuvandikov.datmusic.playback.models.MEDIA_TYPE_AUDIO_QUERY
import com.kuvandikov.datmusic.playback.models.MEDIA_TYPE_DOWNLOADS
import com.kuvandikov.datmusic.playback.models.MEDIA_TYPE_PLAYLIST
import com.kuvandikov.datmusic.playback.models.MediaId
import com.kuvandikov.datmusic.playback.models.PlaybackModeState
import com.kuvandikov.datmusic.playback.models.PlaybackProgressState
import com.kuvandikov.datmusic.playback.models.PlaybackQueue
import com.kuvandikov.datmusic.playback.models.QueueTitle
import com.kuvandikov.datmusic.playback.models.fromMediaController
import com.kuvandikov.datmusic.playback.models.toMediaAudioIds
import com.kuvandikov.datmusic.playback.models.toMediaId
import com.kuvandikov.datmusic.playback.players.AudioPlayer
import com.kuvandikov.datmusic.playback.players.QUEUE_FROM_POSITION_KEY
import com.kuvandikov.datmusic.playback.players.QUEUE_LIST_KEY
import com.kuvandikov.datmusic.playback.players.QUEUE_MEDIA_ID_KEY
import com.kuvandikov.datmusic.playback.players.QUEUE_TITLE_KEY
import com.kuvandikov.datmusic.playback.players.QUEUE_TO_POSITION_KEY
import com.kuvandikov.domain.models.orNull
import com.kuvandikov.i18n.UiMessage

const val PLAYBACK_PROGRESS_INTERVAL = 1000L

interface PlaybackConnection {
    val isConnected: StateFlow<Boolean>
    val playbackState: StateFlow<PlaybackStateCompat>
    val nowPlaying: StateFlow<MediaMetadataCompat>

    val playbackQueue: StateFlow<PlaybackQueue>
    val nowPlayingAudio: StateFlow<PlaybackQueue.NowPlayingAudio?>

    val playbackProgress: StateFlow<PlaybackProgressState>
    val playbackMode: StateFlow<PlaybackModeState>

    val mediaController: MediaControllerCompat?
    val transportControls: MediaControllerCompat.TransportControls?

    fun playAudio(audio: Audio, title: QueueTitle = QueueTitle())
    fun playNextAudio(audio: Audio)
    fun playAudios(audios: List<Audio>, index: Int = 0, title: QueueTitle = QueueTitle())
    fun playArtist(artistId: ArtistId, index: Int = 0)
    fun playPlaylist(playlistId: PlaylistId, index: Int = 0, queue: List<AudioId> = emptyList())
    fun playAlbum(albumId: AlbumId, index: Int = 0)
    fun playFromDownloads(index: Int = 0, queue: List<AudioId> = emptyList())
    fun playWithQuery(query: String, audioId: String)
    fun playWithMinervaQuery(query: String, audioId: String)
    fun playWithFlacsQuery(query: String, audioId: String)

    fun swapQueue(from: Int, to: Int)

    fun removeByPosition(position: Int)
    fun removeById(id: String)
}

class PlaybackConnectionImpl(
    context: Context,
    serviceComponent: ComponentName,
    private val audiosRepo: AudiosRepo,
    private val audioPlayer: AudioPlayer,
    private val downloader: Downloader,
    private val snackbarManager: SnackbarManager,
    coroutineScope: CoroutineScope = ProcessLifecycleOwner.get().lifecycleScope,
) : PlaybackConnection, CoroutineScope by coroutineScope {

    override val isConnected = MutableStateFlow(false)
    override val playbackState = MutableStateFlow(NONE_PLAYBACK_STATE)
    override val nowPlaying = MutableStateFlow(NONE_PLAYING)

    private val playbackQueueState = MutableStateFlow(PlaybackQueue())

    override val playbackQueue = combine(nowPlaying, playbackState, playbackQueueState, ::Triple)
        .map(::buildPlaybackQueue)
        .distinctUntilChanged()
        .stateInDefault(this, PlaybackQueue())

    override val nowPlayingAudio = combine(playbackQueue, playbackState, ::Pair)
        .map { (queue, playbackState) ->
            when (queue.isIndexValid && queue.isValid && !playbackState.isIdle) {
                true -> PlaybackQueue.NowPlayingAudio.from(queue)
                else -> null
            }
        }
        .distinctUntilChanged()
        .stateInDefault(this, null)

    private var playbackProgressInterval: Job = Job()
    override val playbackProgress = MutableStateFlow(PlaybackProgressState())

    override val playbackMode = MutableStateFlow(PlaybackModeState())

    override var mediaController: MediaControllerCompat? = null
    override val transportControls get() = mediaController?.transportControls
    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)
    private val mediaBrowser = MediaBrowserCompat(context, serviceComponent, mediaBrowserConnectionCallback, null).apply { connect() }

    init {
        startPlaybackProgress()
    }

    private fun startPlaybackProgress() = launch {
        combine(playbackState, nowPlaying, ::Pair).collectLatest { (state, current) ->
            playbackProgressInterval.cancel()
            val duration = current.duration
            val position = state.position

            if (state == NONE_PLAYBACK_STATE || current == NONE_PLAYING || duration < 1)
                return@collectLatest

            val initial = PlaybackProgressState(
                total = duration,
                lastPosition = position,
                buffered = audioPlayer.bufferedPosition()
            )
            playbackProgress.value = initial

            if (state.isPlaying && !state.isBuffering)
                starPlaybackProgressInterval(initial)
        }
    }

    /**
     * Resolves audios from given playback queue ids and validates current queues index.
     */
    private suspend fun buildPlaybackQueue(data: Triple<MediaMetadataCompat, PlaybackStateCompat, PlaybackQueue>): PlaybackQueue {
        val audioIdFrequency = mutableMapOf<String, Int>()
        val (nowPlaying, state, queue) = data
        val nowPlayingId = nowPlaying.id.toMediaId().value
        val audios = audiosRepo.find(queue.ids.toMediaAudioIds()).map {
            // build a stable id from audio id and it's frequency
            audioIdFrequency[it.id] = (audioIdFrequency[it.id] ?: -1) + 1
            val newId = it.id + '_' + (audioIdFrequency[it.id])
            it.copy(primaryKey = newId).apply {
                if (id == nowPlayingId) {
                    audioDownloadItem = downloader.getAudioDownload(nowPlayingId).orNull()
                }
            }
        }

        return queue.copy(audios = audios, currentIndex = state.currentIndex).let {
            // check if now playing id and current audio's id by index matches
            val synced = when {
                it.isEmpty() -> false
                state.currentIndex >= it.size -> false
                else -> nowPlayingId == it[state.currentIndex].id
            }

            // if not, try to override current index by finding audio via now playing id
            when (synced) {
                true -> it
                else -> it.copy(isIndexValid = false, currentIndex = it.indexOfFirst { a -> a.id == nowPlayingId })
            }
        }
    }

    private fun starPlaybackProgressInterval(initial: PlaybackProgressState) {
        playbackProgressInterval = launch {
            flowInterval(PLAYBACK_PROGRESS_INTERVAL).collectLatest { ticks ->
                val elapsed = PLAYBACK_PROGRESS_INTERVAL * (ticks + 1)
                playbackProgress.value = initial.copy(elapsed = elapsed, buffered = audioPlayer.bufferedPosition())
            }
        }
    }

    override fun playAudio(audio: Audio, title: QueueTitle) = playAudios(audios = listOf(audio), index = 0, title = title)

    override fun playAudios(audios: List<Audio>, index: Int, title: QueueTitle) {
        val audiosIds = audios.map { it.id }.toTypedArray()
        val audio = audios[index]
        transportControls?.playFromMediaId(
            MediaId(MEDIA_TYPE_AUDIO, audio.id).toString(),
            Bundle().apply {
                putStringArray(QUEUE_LIST_KEY, audiosIds)
                putString(QUEUE_TITLE_KEY, title.toString())
            }
        )
    }

    override fun playNextAudio(audio: Audio) {
        snackbarManager.addMessage(UiMessage.Resource(R.string.audio_menu_playNext_message))
        transportControls?.sendCustomAction(
            PLAY_NEXT,
            bundleOf(
                QUEUE_MEDIA_ID_KEY to audio.id
            )
        )
    }

    override fun playPlaylist(playlistId: PlaylistId, index: Int, queue: List<AudioId>) {
        transportControls?.playFromMediaId(
            MediaId(MEDIA_TYPE_PLAYLIST, playlistId.toString(), index).toString(),
            Bundle().apply {
                if (queue.isNotEmpty())
                    putStringArray(QUEUE_LIST_KEY, queue.toTypedArray())
            }
        )
    }

    override fun playArtist(artistId: ArtistId, index: Int) {
        transportControls?.playFromMediaId(MediaId(MEDIA_TYPE_ARTIST, artistId, index).toString(), null)
    }

    override fun playAlbum(albumId: AlbumId, index: Int) {
        transportControls?.playFromMediaId(MediaId(MEDIA_TYPE_ALBUM, albumId, index).toString(), null)
    }

    override fun playFromDownloads(index: Int, queue: List<AudioId>) {
        transportControls?.playFromMediaId(
            MediaId(MEDIA_TYPE_DOWNLOADS, index = index).toString(),
            Bundle().apply {
                if (queue.isNotEmpty())
                    putStringArray(QUEUE_LIST_KEY, queue.toTypedArray())
            }
        )
    }

    override fun playWithQuery(query: String, audioId: String) {
        transportControls?.playFromMediaId(
            MediaId(MEDIA_TYPE_AUDIO_QUERY, query, -1).toString(),
            bundleOf(
                QUEUE_MEDIA_ID_KEY to audioId
            )
        )
    }

    override fun playWithMinervaQuery(query: String, audioId: String) {
        transportControls?.playFromMediaId(
            MediaId(MEDIA_TYPE_AUDIO_MINERVA_QUERY, query, -1).toString(),
            bundleOf(
                QUEUE_MEDIA_ID_KEY to audioId
            )
        )
    }

    override fun playWithFlacsQuery(query: String, audioId: String) {
        transportControls?.playFromMediaId(
            MediaId(MEDIA_TYPE_AUDIO_FLACS_QUERY, query, -1).toString(),
            bundleOf(
                QUEUE_MEDIA_ID_KEY to audioId
            )
        )
    }

    override fun swapQueue(from: Int, to: Int) {
        transportControls?.sendCustomAction(
            SWAP_ACTION,
            bundleOf(
                QUEUE_FROM_POSITION_KEY to from,
                QUEUE_TO_POSITION_KEY to to,
            )
        )
    }

    override fun removeByPosition(position: Int) {
        transportControls?.sendCustomAction(
            REMOVE_QUEUE_ITEM_BY_POSITION,
            bundleOf(
                QUEUE_FROM_POSITION_KEY to position,
            )
        )
    }

    override fun removeById(id: String) {
        transportControls?.sendCustomAction(
            REMOVE_QUEUE_ITEM_BY_ID,
            bundleOf(
                QUEUE_MEDIA_ID_KEY to id,
            )
        )
    }

    private inner class MediaBrowserConnectionCallback(private val context: Context) :
        MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallback())
            }

            isConnected.value = true
        }

        override fun onConnectionSuspended() {
            isConnected.value = false
        }

        override fun onConnectionFailed() {
            isConnected.value = false
        }
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            playbackState.value = state ?: return
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            nowPlaying.value = metadata ?: return
        }

        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
            Timber.d("New queue: size=${queue?.size}")
            val newQueue = fromMediaController(mediaController ?: return)
            this@PlaybackConnectionImpl.playbackQueueState.value = newQueue
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            playbackMode.value = playbackMode.value.copy(repeatMode = repeatMode)
        }

        override fun onShuffleModeChanged(shuffleMode: Int) {
            playbackMode.value = playbackMode.value.copy(shuffleMode = shuffleMode)
        }

        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }
}
