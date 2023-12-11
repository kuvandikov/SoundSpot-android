/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data.interactors.backup

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext
import com.kuvandikov.base.util.CoroutineDispatchers
import com.kuvandikov.base.util.extensions.readFromFile
import com.kuvandikov.data.AsyncInteractor
import com.kuvandikov.data.ResultInteractor
import com.kuvandikov.datmusic.data.db.daos.AudiosDao
import com.kuvandikov.datmusic.data.db.daos.PlaylistsDao
import com.kuvandikov.datmusic.data.db.daos.PlaylistsWithAudiosDao
import com.kuvandikov.datmusic.data.repos.playlist.PlaylistsRepo
import com.kuvandikov.datmusic.domain.entities.DatmusicBackupData

class RestoreDatmusicBackup @Inject constructor(
    private val audiosDao: AudiosDao,
    private val playlistsDao: PlaylistsDao,
    private val playlistWithAudiosDao: PlaylistsWithAudiosDao,
    private val playlistsRepo: PlaylistsRepo,
    private val dispatchers: CoroutineDispatchers,
) : ResultInteractor<DatmusicBackupData, Pair<Int, Int>>() {
    override suspend fun doWork(params: DatmusicBackupData) = withContext(dispatchers.io) {
        var (deletedCount, insertedCount) = 0 to 0

        deletedCount += audiosDao.deleteAll()
        deletedCount += playlistsDao.deleteAll()
        deletedCount += playlistWithAudiosDao.deleteAll()

        insertedCount += audiosDao.insertAll(params.audios).size
        insertedCount += playlistsDao.insertAll(params.playlists).size
        insertedCount += playlistWithAudiosDao.insertAll(params.playlistAudios).size

        playlistsRepo.regeneratePlaylistArtworks()

        return@withContext deletedCount to insertedCount
    }
}

class RestoreDatmusicFromFile @Inject constructor(
    @ApplicationContext private val context: Context,
    private val restoreDatmusicBackup: RestoreDatmusicBackup,
    private val dispatchers: CoroutineDispatchers,
) : AsyncInteractor<Uri, Pair<Int, Int>>() {

    private val warningState = Channel<Throwable?>(Channel.CONFLATED)
    val warnings = warningState.receiveAsFlow()

    override suspend fun doWork(params: Uri) = withContext(dispatchers.io) {
        val backupJson = context.readFromFile(params)
        val datmusicBackup = DatmusicBackupData.fromJson(backupJson)

        runCatching {
            datmusicBackup.checkVersion()
        }.onFailure {
            warningState.send(it)
        }

        return@withContext restoreDatmusicBackup.execute(datmusicBackup)
    }
}
