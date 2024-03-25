/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.downloader.interactors

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import com.kuvandikov.data.SubjectInteractor
import com.kuvandikov.soundspot.data.db.daos.AudiosFtsDao
import com.kuvandikov.soundspot.domain.entities.DownloadRequest

class SearchDownloads @Inject constructor(
    private val audiosFtsDao: AudiosFtsDao,
) : SubjectInteractor<String, List<DownloadRequest>>() {
    override fun createObservable(params: String): Flow<List<DownloadRequest>> {
        return audiosFtsDao.searchDownloads("*$params*")
    }
}
