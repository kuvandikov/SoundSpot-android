/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.downloader

import com.kuvandikov.datmusic.downloader.DownloaderEvent.ChooseDownloadsLocation.message
import com.kuvandikov.i18n.UiMessage
import com.kuvandikov.i18n.UiMessageConvertable

typealias DownloaderEvents = List<DownloaderEvent>

data class DownloaderEventsError(val events: DownloaderEvents) : Throwable(), UiMessageConvertable {
    override fun toUiMessage() = events.first().toUiMessage()
}

sealed class DownloaderEvent : UiMessageConvertable {
    object ChooseDownloadsLocation : DownloaderEvent() {
        val message = UiMessage.Resource(R.string.downloader_enqueue_downloadsLocationNotSelected)
    }

    data class DownloaderFetchError(val error: Throwable) : DownloaderEvent()

    override fun toUiMessage() = when (this) {
        is ChooseDownloadsLocation -> message
        is DownloaderFetchError -> UiMessage.Error(this.error)
    }
}
