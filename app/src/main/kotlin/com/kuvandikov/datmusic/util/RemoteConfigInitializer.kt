/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.util

import android.app.Application
import javax.inject.Inject
import com.kuvandikov.base.inititializer.AppInitializer
import com.kuvandikov.data.RemoteConfig

class RemoteConfigInitializer @Inject constructor(private val remoteConfig: RemoteConfig) : AppInitializer {
    override fun init(application: Application) {
        remoteConfig
    }
}
