/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data.observers.playlist

import com.kuvandikov.datmusic.downloader.R
import com.kuvandikov.i18n.UiMessage
import com.kuvandikov.i18n.ValidationError
import com.kuvandikov.i18n.ValidationErrorException

data class NoResultsForPlaylistFilter(val params: ObservePlaylistDetails.Params) :
    ValidationErrorException(
        ValidationError(
            when (params.hasQuery) {
                true -> UiMessage.Resource(
                    R.string.playlist_detail_filter_noResults_forQuery,
                    listOf(params.query)
                )
                else -> UiMessage.Resource(R.string.playlist_detail_filter_noResults)
            }
        )
    )
