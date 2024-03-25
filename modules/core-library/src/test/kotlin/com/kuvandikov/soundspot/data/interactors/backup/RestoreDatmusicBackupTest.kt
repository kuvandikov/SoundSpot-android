/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.interactors.backup

import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import javax.inject.Inject
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
class RestoreSoundspotBackupTest : BaseTest() {

    @Inject lateinit var database: AppDatabase
    @Inject lateinit var playlistsRepo: PlaylistsRepo
    @Inject lateinit var audiosRepo: AudiosRepo
    @Inject lateinit var artistsDao: ArtistsDao
    @Inject lateinit var albumsDao: AlbumsDao
    @Inject lateinit var downloadRequestsDao: DownloadRequestsDao
    @Inject lateinit var createSoundspotBackup: CreateSoundspotBackup
    @Inject lateinit var restoreSoundspotBackup: RestoreSoundspotBackup

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `restored backup is the same as initial backup after clearing & restoring from initial backup`() = runTest {
        createBackupData(playlistsRepo, audiosRepo, artistsDao, albumsDao, downloadRequestsDao)
        val initialBackup = createSoundspotBackup.execute(Unit)
        database.clearAllTables()

        restoreSoundspotBackup.execute(initialBackup)

        val restoredBackup = createSoundspotBackup.execute(Unit)
        assertThat(restoredBackup)
            .isEqualTo(initialBackup)
    }
}
