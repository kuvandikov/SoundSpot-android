/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.domain

import com.kuvandikov.coreDomain.R

enum class DownloadsSongsGrouping(val labelRes: Int, val exampleRes: Int) {
    ByAlbum(R.string.settings_downloads_songsGrouping_byAlbum, R.string.settings_downloads_songsGrouping_byAlbum_example),
    ByArtist(R.string.settings_downloads_songsGrouping_byArtist, R.string.settings_downloads_songsGrouping_byArtist_example),
    Flat(R.string.settings_downloads_songsGrouping_flat, R.string.settings_downloads_songsGrouping_flat_example);

    companion object {
        val Default = Flat
        val map = values().associateBy { it.name }

        fun from(value: String) = map[value] ?: Default
    }
}
