/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.playback.players

import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import androidx.core.os.bundleOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import timber.log.Timber
import com.kuvandikov.base.util.extensions.readable
import com.kuvandikov.soundspot.playback.AudioFocusHelper
import com.kuvandikov.soundspot.playback.BY_UI_KEY
import com.kuvandikov.soundspot.playback.PAUSE_ACTION
import com.kuvandikov.soundspot.playback.PLAY_ACTION
import com.kuvandikov.soundspot.playback.PLAY_ALL_SHUFFLED
import com.kuvandikov.soundspot.playback.PLAY_NEXT
import com.kuvandikov.soundspot.playback.REMOVE_QUEUE_ITEM_BY_ID
import com.kuvandikov.soundspot.playback.REMOVE_QUEUE_ITEM_BY_POSITION
import com.kuvandikov.soundspot.playback.REPEAT_ALL
import com.kuvandikov.soundspot.playback.REPEAT_ONE
import com.kuvandikov.soundspot.playback.SET_MEDIA_STATE
import com.kuvandikov.soundspot.playback.SWAP_ACTION
import com.kuvandikov.soundspot.playback.UPDATE_QUEUE
import com.kuvandikov.soundspot.playback.isPlaying
import com.kuvandikov.soundspot.playback.models.toMediaIdList

const val SEEK_TO = "action_seek_to"

const val QUEUE_MEDIA_ID_KEY = "queue_media_id_key"
const val QUEUE_TITLE_KEY = "queue_title_key"
const val QUEUE_LIST_KEY = "queue_list_key"

const val QUEUE_FROM_POSITION_KEY = "queue_from_position_key"
const val QUEUE_TO_POSITION_KEY = "queue_to_position_key"

class MediaSessionCallback(
    private val mediaSession: MediaSessionCompat,
    private val soundspotPlayer: SoundspotPlayer,
    private val audioFocusHelper: AudioFocusHelper,
) : MediaSessionCompat.Callback(), CoroutineScope by MainScope() {

    init {
        audioFocusHelper.onAudioFocusGain {
            Timber.d("GAIN")
            if (isAudioFocusGranted && !soundspotPlayer.getSession().isPlaying()) {
                soundspotPlayer.playAudio()
            } else audioFocusHelper.setVolume(AudioManager.ADJUST_RAISE)
            isAudioFocusGranted = false
        }
        audioFocusHelper.onAudioFocusLoss {
            Timber.d("LOSS")
            abandonPlayback()
            isAudioFocusGranted = false
            soundspotPlayer.pause()
        }

        audioFocusHelper.onAudioFocusLossTransient {
            Timber.d("TRANSIENT")
            if (soundspotPlayer.getSession().isPlaying()) {
                isAudioFocusGranted = true
                soundspotPlayer.pause()
            }
        }

        audioFocusHelper.onAudioFocusLossTransientCanDuck {
            Timber.d("TRANSIENT_CAN_DUCK")
            audioFocusHelper.setVolume(AudioManager.ADJUST_LOWER)
        }
    }

    override fun onPause() {
        Timber.d("onPause")
        soundspotPlayer.pause()
    }

    override fun onPlay() {
        Timber.d("onPlay")
        playOnFocus()
    }

    override fun onPlayFromSearch(query: String?, extras: Bundle?) {
        Timber.d("onPlayFromSearch, query = $query, ${extras?.readable()}")
        query?.let {
            // val audio = findAudioForQuery(query)
            // if (audio != null) {
            //     launch {
            //         musicPlayer.playAudio(audio)
            //     }
            // }
        } ?: onPlay()
    }

    override fun onFastForward() {
        Timber.d("onFastForward")
        soundspotPlayer.fastForward()
    }

    override fun onRewind() {
        Timber.d("onRewind")
        soundspotPlayer.rewind()
    }

    override fun onPlayFromMediaId(mediaId: String, extras: Bundle?) {
        Timber.d("onPlayFromMediaId, $mediaId, ${extras?.readable()}")
        launch { soundspotPlayer.setDataFromMediaId(mediaId, extras ?: bundleOf()) }
    }

    override fun onSeekTo(position: Long) {
        Timber.d("onSeekTo: position=$position")
        soundspotPlayer.seekTo(position)
    }

    override fun onSkipToNext() {
        Timber.d("onSkipToNext()")
        launch { soundspotPlayer.nextAudio() }
    }

    override fun onSkipToPrevious() {
        Timber.d("onSkipToPrevious()")
        launch { soundspotPlayer.previousAudio() }
    }

    override fun onSkipToQueueItem(id: Long) {
        Timber.d("onSkipToQueueItem: $id")
        launch { soundspotPlayer.skipTo(id.toInt()) }
    }

    override fun onStop() {
        Timber.d("onStop()")
        soundspotPlayer.stop(byUser = true)
    }

    override fun onSetRepeatMode(repeatMode: Int) {
        super.onSetRepeatMode(repeatMode)
        val bundle = mediaSession.controller.playbackState.extras ?: Bundle()
        soundspotPlayer.setPlaybackState(
            Builder(mediaSession.controller.playbackState)
                .setExtras(
                    bundle.apply {
                        putInt(REPEAT_MODE, repeatMode)
                    }
                ).build()
        )
    }

    override fun onSetShuffleMode(shuffleMode: Int) {
        super.onSetShuffleMode(shuffleMode)
        soundspotPlayer.setShuffleMode(shuffleMode)
    }

    override fun onCustomAction(action: String?, extras: Bundle?) {
        when (action) {
            SET_MEDIA_STATE -> launch { setSavedMediaSessionState() }
            REPEAT_ONE -> launch { soundspotPlayer.repeatAudio() }
            REPEAT_ALL -> launch { soundspotPlayer.repeatQueue() }
            PAUSE_ACTION -> soundspotPlayer.pause(extras ?: bundleOf(BY_UI_KEY to true))
            PLAY_ACTION -> playOnFocus(extras ?: bundleOf(BY_UI_KEY to true))
            PLAY_NEXT -> soundspotPlayer.playNext(extras?.getString(QUEUE_MEDIA_ID_KEY) ?: return)
            REMOVE_QUEUE_ITEM_BY_POSITION -> soundspotPlayer.removeFromQueue(extras?.getInt(QUEUE_FROM_POSITION_KEY) ?: return)
            REMOVE_QUEUE_ITEM_BY_ID -> soundspotPlayer.removeFromQueue(extras?.getString(QUEUE_MEDIA_ID_KEY) ?: return)
            UPDATE_QUEUE -> {
                extras ?: return

                val queue = extras.getStringArray(QUEUE_LIST_KEY)?.toList() ?: emptyList()
                val queueTitle = extras.getString(QUEUE_TITLE_KEY)

                soundspotPlayer.updateData(queue, queueTitle)
            }
            PLAY_ALL_SHUFFLED -> {
                extras ?: return

                val controller = mediaSession.controller ?: return

                val queue = extras.getStringArray(QUEUE_LIST_KEY)?.toList() ?: emptyList()
                val queueTitle = extras.getString(QUEUE_TITLE_KEY)
                soundspotPlayer.setData(queue, queueTitle)

                controller.transportControls.setShuffleMode(SHUFFLE_MODE_ALL)

                launch {
                    soundspotPlayer.nextAudio()
                }
            }
            SWAP_ACTION -> {
                extras ?: return
                val from = extras.getInt(QUEUE_FROM_POSITION_KEY)
                val to = extras.getInt(QUEUE_TO_POSITION_KEY)

                soundspotPlayer.swapQueueAudios(from, to)
            }
        }
    }

    private suspend fun setSavedMediaSessionState() {
        val controller = mediaSession.controller ?: return
        Timber.d(controller.playbackState.toString())
        if (controller.playbackState == null || controller.playbackState.state == STATE_NONE) {
            soundspotPlayer.restoreQueueState()
        } else {
            restoreMediaSession()
        }
    }

    private fun restoreMediaSession() {
        mediaSession.setMetadata(mediaSession.controller.metadata)
        soundspotPlayer.setPlaybackState(mediaSession.controller.playbackState)
        soundspotPlayer.setData(
            mediaSession.controller?.queue.toMediaIdList().map { it.value },
            mediaSession.controller?.queueTitle.toString()
        )
    }

    private fun playOnFocus(extras: Bundle = bundleOf(BY_UI_KEY to true)) {
        if (audioFocusHelper.requestPlayback())
            soundspotPlayer.playAudio(extras)
    }
}
