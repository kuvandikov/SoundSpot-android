/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.downloader.interactors

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import com.kuvandikov.data.SubjectInteractor
import com.kuvandikov.datmusic.data.db.daos.AudiosFtsDao
import com.kuvandikov.datmusic.domain.entities.DownloadRequest

class SearchDownloads @Inject constructor(
    private val audiosFtsDao: AudiosFtsDao,
) : SubjectInteractor<String, List<DownloadRequest>>() {
    override fun createObservable(params: String): Flow<List<DownloadRequest>> {
        return audiosFtsDao.searchDownloads("*$params*")
    }
}
