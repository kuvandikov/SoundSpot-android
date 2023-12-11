/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic

import android.content.Context
import androidx.multidex.MultiDex
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import com.kuvandikov.base.BaseApp
import com.kuvandikov.base.inititializer.AppInitializers
import com.kuvandikov.base.migrator.AppMigrator

@HiltAndroidApp
class App : BaseApp() {

    @Inject
    lateinit var initializers: AppInitializers

    @Inject
    lateinit var appMigrator: AppMigrator

    override fun onCreate() {
        super.onCreate()
        initializers.init(this)
        appMigrator.migrate()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
