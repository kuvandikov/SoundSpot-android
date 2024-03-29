/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.playback.services

import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.kuvandikov.base.util.CoroutineDispatchers
import com.kuvandikov.soundspot.playback.MediaNotificationsImpl
import com.kuvandikov.soundspot.playback.MediaQueueBuilder
import com.kuvandikov.soundspot.playback.NEXT
import com.kuvandikov.soundspot.playback.NOTIFICATION_ID
import com.kuvandikov.soundspot.playback.PLAY_PAUSE
import com.kuvandikov.soundspot.playback.PREVIOUS
import com.kuvandikov.soundspot.playback.STOP_PLAYBACK
import com.kuvandikov.soundspot.playback.isIdle
import com.kuvandikov.soundspot.playback.models.MediaId
import com.kuvandikov.soundspot.playback.models.MediaId.Companion.CALLER_OTHER
import com.kuvandikov.soundspot.playback.models.MediaId.Companion.CALLER_SELF
import com.kuvandikov.soundspot.playback.models.toMediaId
import com.kuvandikov.soundspot.playback.models.toMediaItems
import com.kuvandikov.soundspot.playback.playPause
import com.kuvandikov.soundspot.playback.players.SoundspotPlayerImpl
import com.kuvandikov.soundspot.playback.receivers.BecomingNoisyReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PlayerService : MediaBrowserServiceCompat(), CoroutineScope by MainScope() {

    companion object {
        var IS_FOREGROUND = false
    }

    @Inject
    protected lateinit var dispatchers: CoroutineDispatchers

    @Inject
    protected lateinit var soundspotPlayer: SoundspotPlayerImpl

    @Inject
    protected lateinit var mediaNotifications: MediaNotificationsImpl

    @Inject
    protected lateinit var mediaQueueBuilder: MediaQueueBuilder

    private lateinit var becomingNoisyReceiver: BecomingNoisyReceiver

    override fun onCreate() {
        super.onCreate()

        sessionToken = soundspotPlayer.getSession().sessionToken
        becomingNoisyReceiver = BecomingNoisyReceiver(this, sessionToken!!)

        soundspotPlayer.onPlayingState { isPlaying, byUi ->
            val isIdle = soundspotPlayer.getSession().controller.playbackState.isIdle
            if (!isPlaying && isIdle) {
                pauseForeground(byUi)
                mediaNotifications.clearNotifications()
            } else {
                startForeground()
            }

            mediaNotifications.updateNotification(getSession())
        }

        soundspotPlayer.onMetaDataChanged {
            mediaNotifications.updateNotification(getSession())
        }
    }

    private fun startForeground() {
        if (IS_FOREGROUND) {
            Timber.w("Tried to start foreground, but was already in foreground")
            return
        }
        Timber.d("Starting foreground service")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, mediaNotifications.buildNotification(soundspotPlayer.getSession()),ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        }
        else{
            startForeground(NOTIFICATION_ID, mediaNotifications.buildNotification(soundspotPlayer.getSession()))
        }
        becomingNoisyReceiver.register()
        IS_FOREGROUND = true
    }

    private fun pauseForeground(removeNotification: Boolean) {
        if (!IS_FOREGROUND) {
            Timber.w("Tried to stop foreground, but was already NOT in foreground")
            return
        }
        Timber.d("Stopping foreground service")
        becomingNoisyReceiver.unregister()
        stopForeground(removeNotification)
        IS_FOREGROUND = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            return START_STICKY
        }

        val mediaSession = soundspotPlayer.getSession()
        val controller = mediaSession.controller

        when (intent.action) {
            PLAY_PAUSE -> controller.playPause()
            NEXT -> controller.transportControls.skipToNext()
            PREVIOUS -> controller.transportControls.skipToPrevious()
            STOP_PLAYBACK -> controller.transportControls.stop()
        }

        MediaButtonReceiver.handleIntent(mediaSession, intent)
        return START_STICKY
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        val caller = if (clientPackageName == applicationContext.packageName) CALLER_SELF else CALLER_OTHER
        return BrowserRoot(MediaId("-1", caller = caller).toString(), null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        result.detach()
        launch {
            val itemList = withContext(dispatchers.io) { loadChildren(parentId) }
            result.sendResult(itemList)
        }
    }

    private suspend fun loadChildren(parentId: String): MutableList<MediaBrowserCompat.MediaItem> {
        val list = mutableListOf<MediaBrowserCompat.MediaItem>()
        val mediaId = parentId.toMediaId()
        list.addAll(mediaQueueBuilder.buildAudioList(mediaId).toMediaItems())
        return list
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        launch {
            soundspotPlayer.pause()
            soundspotPlayer.saveQueueState()
            soundspotPlayer.stop(byUser = false)
        }
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        launch {
            soundspotPlayer.saveQueueState()
            soundspotPlayer.release()
        }
    }
}
