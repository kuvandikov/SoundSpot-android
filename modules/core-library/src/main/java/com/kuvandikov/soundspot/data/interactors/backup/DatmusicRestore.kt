/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.interactors.backup

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
import com.kuvandikov.soundspot.data.db.daos.AudiosDao
import com.kuvandikov.soundspot.data.db.daos.PlaylistsDao
import com.kuvandikov.soundspot.data.db.daos.PlaylistsWithAudiosDao
import com.kuvandikov.soundspot.data.repos.playlist.PlaylistsRepo
import com.kuvandikov.soundspot.domain.entities.SoundspotBackupData

class RestoreSoundspotBackup @Inject constructor(
    private val audiosDao: AudiosDao,
    private val playlistsDao: PlaylistsDao,
    private val playlistWithAudiosDao: PlaylistsWithAudiosDao,
    private val playlistsRepo: PlaylistsRepo,
    private val dispatchers: CoroutineDispatchers,
) : ResultInteractor<SoundspotBackupData, Pair<Int, Int>>() {
    override suspend fun doWork(params: SoundspotBackupData) = withContext(dispatchers.io) {
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

class RestoreSoundspotFromFile @Inject constructor(
    @ApplicationContext private val context: Context,
    private val restoreSoundspotBackup: RestoreSoundspotBackup,
    private val dispatchers: CoroutineDispatchers,
) : AsyncInteractor<Uri, Pair<Int, Int>>() {

    private val warningState = Channel<Throwable?>(Channel.CONFLATED)
    val warnings = warningState.receiveAsFlow()

    override suspend fun doWork(params: Uri) = withContext(dispatchers.io) {
        val backupJson = context.readFromFile(params)
        val soundspotBackup = SoundspotBackupData.fromJson(backupJson)

        runCatching {
            soundspotBackup.checkVersion()
        }.onFailure {
            warningState.send(it)
        }

        return@withContext restoreSoundspotBackup.execute(soundspotBackup)
    }
}
