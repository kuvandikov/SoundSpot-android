/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.downloader

import androidx.compose.runtime.staticCompositionLocalOf
import com.kuvandikov.soundspot.downloader.Downloader

val LocalDownloader = staticCompositionLocalOf<Downloader> {
    error("LocalDownloader not provided")
}
