/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.downloader

import javax.inject.Inject
import com.kuvandikov.base.util.CoroutineDispatchers
import com.kuvandikov.data.db.RoomRepo
import com.kuvandikov.datmusic.data.db.daos.DownloadRequestsDao
import com.kuvandikov.datmusic.domain.entities.DownloadRequest

class DownloadRequestsRepo @Inject constructor(
    dispatchers: CoroutineDispatchers,
    dao: DownloadRequestsDao,
) : RoomRepo<String, DownloadRequest>(dao, dispatchers)
