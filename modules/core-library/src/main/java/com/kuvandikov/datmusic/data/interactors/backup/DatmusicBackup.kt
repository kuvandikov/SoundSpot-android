/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data.interactors.backup

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import com.kuvandikov.base.util.CoroutineDispatchers
import com.kuvandikov.base.util.extensions.writeToFile
import com.kuvandikov.data.AsyncInteractor
import com.kuvandikov.data.LastRequests
import com.kuvandikov.data.PreferencesStore
import com.kuvandikov.data.ResultInteractor
import com.kuvandikov.datmusic.coreLibrary.R
import com.kuvandikov.datmusic.data.db.daos.AudiosDao
import com.kuvandikov.datmusic.data.db.daos.DownloadRequestsDao
import com.kuvandikov.datmusic.data.db.daos.PlaylistsDao
import com.kuvandikov.datmusic.data.db.daos.PlaylistsWithAudiosDao
import com.kuvandikov.datmusic.data.interactors.playlist.CreateOrGetPlaylist
import com.kuvandikov.datmusic.domain.entities.DatmusicBackupData
import com.kuvandikov.datmusic.domain.entities.DownloadRequest

class CreateDatmusicBackup @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchers: CoroutineDispatchers,
    private val preferencesStore: PreferencesStore,
    private val audiosDao: AudiosDao,
    private val playlistsDao: PlaylistsDao,
    private val playlistWithAudiosDao: PlaylistsWithAudiosDao,
    private val downloadRequestsDao: DownloadRequestsDao,
    private val clearUnusedEntities: ClearUnusedEntities,
    private val createOrGetPlaylist: CreateOrGetPlaylist,
) : ResultInteractor<Unit, DatmusicBackupData>() {

    override suspend fun doWork(params: Unit) = withContext(dispatchers.io) {
        clearUnusedEntities()
        LastRequests.clearAll(preferencesStore)

        val downloadRequestAudios = downloadRequestsDao.getByType(DownloadRequest.Type.Audio)
        val downloadedAudioIds = downloadRequestAudios.map { it.id }

        if (downloadedAudioIds.isNotEmpty())
            createOrGetPlaylist.execute(
                CreateOrGetPlaylist.Params(
                    name = context.getString(R.string.playlist_create_downloadsBackupTemplate),
                    audioIds = downloadedAudioIds,
                    ignoreExistingAudios = true,
                )
            )

        val audios = audiosDao.entries().first()
        val playlists = playlistsDao.entries().first().map { it.copyForBackup() }
        val playlistAudios = playlistWithAudiosDao.playlistAudios().first()

        return@withContext DatmusicBackupData.create(
            audios = audios,
            playlists = playlists,
            playlistAudios = playlistAudios
        )
    }
}

class CreateDatmusicBackupToFile @Inject constructor(
    @ApplicationContext private val context: Context,
    private val createDatmusicBackup: CreateDatmusicBackup,
    private val dispatchers: CoroutineDispatchers,
) : AsyncInteractor<Uri, Unit>() {

    override suspend fun doWork(params: Uri) = withContext(dispatchers.io) {
        val backup = createDatmusicBackup.execute(Unit)
        val backupJsonBytes = backup.toJson().toByteArray()
        context.writeToFile(backupJsonBytes, params)
    }
}
