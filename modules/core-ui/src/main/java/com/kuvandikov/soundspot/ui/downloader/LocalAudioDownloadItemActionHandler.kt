/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.downloader

import androidx.compose.runtime.staticCompositionLocalOf
import com.kuvandikov.soundspot.ui.downloads.AudioDownloadItemAction

val LocalAudioDownloadItemActionHandler = staticCompositionLocalOf<AudioDownloadItemActionHandler> {
    error("No AudioDownloadItemActionHandler provided")
}

typealias AudioDownloadItemActionHandler = (AudioDownloadItemAction) -> Unit
