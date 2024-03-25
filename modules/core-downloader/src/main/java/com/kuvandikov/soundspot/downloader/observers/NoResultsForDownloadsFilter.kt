/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.downloader.observers

import com.kuvandikov.soundspot.downloader.R
import com.kuvandikov.i18n.UiMessage
import com.kuvandikov.i18n.ValidationError
import com.kuvandikov.i18n.ValidationErrorException

data class NoResultsForDownloadsFilter(val params: ObserveDownloads.Params) :
    ValidationErrorException(
        ValidationError(
            when (params.hasQuery) {
                true -> UiMessage.Resource(
                    R.string.downloads_filter_noResults_forQuery,
                    listOf(params.query)
                )
                else -> UiMessage.Resource(R.string.downloads_filter_noResults)
            }
        )
    )
