/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov

import org.threeten.bp.Duration

object Config {
    const val BASE_HOST = "datmusic.xyz"
    const val BASE_URL = "https://$BASE_HOST/"
    const val API_BASE_URL = "https://api-demo.$BASE_HOST/"

    const val PLAYSTORE_ID = "com.kuvandikov.datmusic"
    const val PLAYSTORE_URL = "https://play.google.com/store/apps/details?id=$PLAYSTORE_ID"

    val API_TIMEOUT = Duration.ofSeconds(40).toMillis()
    val DOWNLOADER_TIMEOUT = Duration.ofMinutes(3).toMillis()
    val PLAYER_TIMEOUT = Duration.ofMinutes(2).toMillis()
    val PLAYER_TIMEOUT_CONNECT = Duration.ofSeconds(30).toMillis()
}
