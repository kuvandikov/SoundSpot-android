/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.playback.models

import com.kuvandikov.base.util.millisToDuration

data class PlaybackProgressState(
    val total: Long = 0L,
    val lastPosition: Long = 0L,
    val elapsed: Long = 0L,
    val buffered: Long = 0L,
) {

    val progress get() = ((lastPosition.toFloat() + elapsed) / (total + 1).toFloat()).coerceIn(0f, 1f)
    val bufferedProgress get() = ((buffered.toFloat()) / (total + 1).toFloat()).coerceIn(0f, 1f)

    val currentDuration get() = (lastPosition + elapsed).millisToDuration()
    val totalDuration get() = total.millisToDuration()
}
