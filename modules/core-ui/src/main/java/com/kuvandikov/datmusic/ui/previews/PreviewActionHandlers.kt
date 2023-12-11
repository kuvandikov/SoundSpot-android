/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.previews

import timber.log.Timber
import com.kuvandikov.datmusic.ui.audios.AudioActionHandler
import com.kuvandikov.datmusic.ui.audios.AudioItemAction

internal val PreviewAudioActionHandler: AudioActionHandler = { action: AudioItemAction ->
    Timber.d("PreviewAudioActionHandler: $action")
}
