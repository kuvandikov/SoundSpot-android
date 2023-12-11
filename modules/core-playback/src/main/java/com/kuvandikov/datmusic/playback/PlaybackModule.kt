/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.playback

import android.content.ComponentName
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.kuvandikov.base.ui.SnackbarManager
import com.kuvandikov.datmusic.data.repos.audio.AudiosRepo
import com.kuvandikov.datmusic.downloader.Downloader
import com.kuvandikov.datmusic.playback.players.AudioPlayerImpl
import com.kuvandikov.datmusic.playback.services.PlayerService

@InstallIn(SingletonComponent::class)
@Module
class PlaybackModule {

    @Provides
    @Singleton
    fun playbackConnection(
        @ApplicationContext context: Context,
        audiosRepo: AudiosRepo,
        audioPlayer: AudioPlayerImpl,
        downloader: Downloader,
        snackbarManager: SnackbarManager,
    ): PlaybackConnection = PlaybackConnectionImpl(
        context = context,
        serviceComponent = ComponentName(context, PlayerService::class.java),
        audiosRepo = audiosRepo,
        audioPlayer = audioPlayer,
        downloader = downloader,
        snackbarManager = snackbarManager,
    )
}
