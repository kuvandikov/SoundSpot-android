/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import com.kuvandikov.base.util.extensions.stateInDefault
import com.kuvandikov.data.REMOTE_CONFIG_FETCH_DELAY
import com.kuvandikov.data.RemoteConfig
import com.kuvandikov.soundspot.data.config.getSettingsLinks

@HiltViewModel
internal class SettingsViewModel @Inject constructor(remoteConfig: RemoteConfig) : ViewModel() {

    val settingsLinks = flow {
        // initially fetch once then one more time when there might be an update
        emit(remoteConfig.getSettingsLinks())
        delay(REMOTE_CONFIG_FETCH_DELAY)
        emit(remoteConfig.getSettingsLinks())
    }.stateInDefault(viewModelScope, emptyList())
}
