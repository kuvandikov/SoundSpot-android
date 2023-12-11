/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.downloads

import javax.annotation.concurrent.Immutable
import com.kuvandikov.datmusic.downloader.DownloadItems
import com.kuvandikov.datmusic.downloader.observers.ObserveDownloads
import com.kuvandikov.domain.models.Async
import com.kuvandikov.domain.models.Uninitialized

@Immutable
internal data class DownloadsViewState(
    val downloads: Async<DownloadItems> = Uninitialized,
    val params: ObserveDownloads.Params = ObserveDownloads.Params()
) {

    companion object {
        val Empty = DownloadsViewState()
    }
}
