/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.interactors.backup

import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import com.kuvandikov.base.testing.BaseTest
import com.kuvandikov.soundspot.data.db.AppDatabase
import com.kuvandikov.soundspot.data.db.DatabaseModule
import com.kuvandikov.soundspot.data.db.daos.AlbumsDao
import com.kuvandikov.soundspot.data.db.daos.ArtistsDao
import com.kuvandikov.soundspot.data.db.daos.DownloadRequestsDao
import com.kuvandikov.soundspot.data.repos.audio.AudiosRepo
import com.kuvandikov.soundspot.data.repos.playlist.PlaylistsRepo

@HiltAndroidTest
@UninstallModules(DatabaseModule::class)
class CreateSoundspotBackupTest : BaseTest() {

    @Inject lateinit var database: AppDatabase
    @Inject lateinit var playlistsRepo: PlaylistsRepo
    @Inject lateinit var audiosRepo: AudiosRepo
    @Inject lateinit var artistsDao: ArtistsDao
    @Inject lateinit var albumsDao: AlbumsDao
    @Inject lateinit var downloadRequestsDao: DownloadRequestsDao
    @Inject lateinit var createSoundspotBackup: CreateSoundspotBackup

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `creates backup with audios & playlists`() = runTest {
        val (playlistItemAudios, downloadRequests) = createBackupData(playlistsRepo, audiosRepo, artistsDao, albumsDao, downloadRequestsDao)

        val backup = createSoundspotBackup.execute(Unit)

        val playlistAudiosAndDownloads = playlistItemAudios.map { it.id } + downloadRequests.map { it.id }
        assertThat(backup.audios.map { it.id }.toSet())
            .containsExactlyElementsIn(playlistAudiosAndDownloads.toSet())

        assertThat(artistsDao.entries().first())
            .isEmpty()
        assertThat(albumsDao.entries().first())
            .isEmpty()
    }
}
