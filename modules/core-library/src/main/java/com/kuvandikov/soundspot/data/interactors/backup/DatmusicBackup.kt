/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.interactors.backup

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
import com.kuvandikov.soundspot.coreLibrary.R
import com.kuvandikov.soundspot.data.db.daos.AudiosDao
import com.kuvandikov.soundspot.data.db.daos.DownloadRequestsDao
import com.kuvandikov.soundspot.data.db.daos.PlaylistsDao
import com.kuvandikov.soundspot.data.db.daos.PlaylistsWithAudiosDao
import com.kuvandikov.soundspot.data.interactors.playlist.CreateOrGetPlaylist
import com.kuvandikov.soundspot.domain.entities.SoundspotBackupData
import com.kuvandikov.soundspot.domain.entities.DownloadRequest

class CreateSoundspotBackup @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchers: CoroutineDispatchers,
    private val preferencesStore: PreferencesStore,
    private val audiosDao: AudiosDao,
    private val playlistsDao: PlaylistsDao,
    private val playlistWithAudiosDao: PlaylistsWithAudiosDao,
    private val downloadRequestsDao: DownloadRequestsDao,
    private val clearUnusedEntities: ClearUnusedEntities,
    private val createOrGetPlaylist: CreateOrGetPlaylist,
) : ResultInteractor<Unit, SoundspotBackupData>() {

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

        return@withContext SoundspotBackupData.create(
            audios = audios,
            playlists = playlists,
            playlistAudios = playlistAudios
        )
    }
}

class CreateSoundspotBackupToFile @Inject constructor(
    @ApplicationContext private val context: Context,
    private val createSoundspotBackup: CreateSoundspotBackup,
    private val dispatchers: CoroutineDispatchers,
) : AsyncInteractor<Uri, Unit>() {

    override suspend fun doWork(params: Uri) = withContext(dispatchers.io) {
        val backup = createSoundspotBackup.execute(Unit)
        val backupJsonBytes = backup.toJson().toByteArray()
        context.writeToFile(backupJsonBytes, params)
    }
}
