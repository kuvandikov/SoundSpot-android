/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.previews

import timber.log.Timber
import com.kuvandikov.soundspot.ui.downloads.AudioDownloadItemAction

internal val PreviewAudioDownloadItemActionHandler = { action: AudioDownloadItemAction ->
    Timber.d("PreviewAudioDownloadItemActionHandler: $action")
}
