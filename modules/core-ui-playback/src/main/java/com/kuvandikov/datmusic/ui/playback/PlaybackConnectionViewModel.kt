/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.playback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.kuvandikov.datmusic.playback.PlaybackConnection
import com.kuvandikov.datmusic.playback.SET_MEDIA_STATE

@HiltViewModel
class PlaybackConnectionViewModel @Inject constructor(val playbackConnection: PlaybackConnection) : ViewModel() {

    init {
        viewModelScope.launch {
            playbackConnection.isConnected.collectLatest { connected ->
                if (connected) {
                    playbackConnection.transportControls?.sendCustomAction(SET_MEDIA_STATE, null)
                }
            }
        }
    }
}
