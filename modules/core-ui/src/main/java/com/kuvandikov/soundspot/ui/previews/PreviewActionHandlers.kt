/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.previews

import timber.log.Timber
import com.kuvandikov.soundspot.ui.audios.AudioActionHandler
import com.kuvandikov.soundspot.ui.audios.AudioItemAction

internal val PreviewAudioActionHandler: AudioActionHandler = { action: AudioItemAction ->
    Timber.d("PreviewAudioActionHandler: $action")
}
