/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.downloader

import javax.inject.Inject
import com.kuvandikov.base.util.CoroutineDispatchers
import com.kuvandikov.data.db.RoomRepo
import com.kuvandikov.soundspot.data.db.daos.DownloadRequestsDao
import com.kuvandikov.soundspot.domain.entities.DownloadRequest

class DownloadRequestsRepo @Inject constructor(
    dispatchers: CoroutineDispatchers,
    dao: DownloadRequestsDao,
) : RoomRepo<String, DownloadRequest>(dao, dispatchers)
