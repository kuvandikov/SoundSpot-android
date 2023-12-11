/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.playback.models

import android.support.v4.media.session.PlaybackStateCompat

data class PlaybackModeState(
    val shuffleMode: Int = PlaybackStateCompat.SHUFFLE_MODE_INVALID,
    val repeatMode: Int = PlaybackStateCompat.REPEAT_MODE_INVALID,
)
