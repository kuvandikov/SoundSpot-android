/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic

object Config {
    const val APP_USER_AGENT = "Datmusic App/${BuildConfig.VERSION_NAME}-${BuildConfig.VERSION_CODE}"

    val IS_DEBUG = BuildConfig.DEBUG
}

/**
 * Run [block] if app in debug mode.
 */
fun ifDebug(block: () -> Unit) {
    if (Config.IS_DEBUG) block()
}
