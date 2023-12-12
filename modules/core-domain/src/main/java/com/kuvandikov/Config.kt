/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov

import org.threeten.bp.Duration

object Config {
//    https://1AXPVLGBOB-dsn.algolia.net/1/indexes/songs/query
    const val BASE_HOST = "algolia.net"
    const val BASE_URL = "https://$BASE_HOST/"
    const val API_BASE_URL = "https://1AXPVLGBOB-dsn.$BASE_HOST/"

    const val PLAYSTORE_ID = "com.kuvandikov.datmusic"
    const val PLAYSTORE_URL = "https://play.google.com/store/apps/details?id=$PLAYSTORE_ID"

    val API_TIMEOUT = Duration.ofSeconds(40).toMillis()
    val DOWNLOADER_TIMEOUT = Duration.ofMinutes(3).toMillis()
    val PLAYER_TIMEOUT = Duration.ofMinutes(2).toMillis()
    val PLAYER_TIMEOUT_CONNECT = Duration.ofSeconds(30).toMillis()
}
