/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.audios

import androidx.compose.runtime.staticCompositionLocalOf

val LocalAudioActionHandler = staticCompositionLocalOf<AudioActionHandler> {
    error("No LocalAudioActionHandler provided")
}

typealias AudioActionHandler = (AudioItemAction) -> Unit
