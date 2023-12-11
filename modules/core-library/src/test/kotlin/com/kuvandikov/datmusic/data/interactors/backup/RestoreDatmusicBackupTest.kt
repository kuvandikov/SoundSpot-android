/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data.interactors.backup

import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import javax.inject.Inject
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import com.kuvandikov.base.testing.BaseTest
import com.kuvandikov.datmusic.data.db.AppDatabase
import com.kuvandikov.datmusic.data.db.DatabaseModule
import com.kuvandikov.datmusic.data.db.daos.AlbumsDao
import com.kuvandikov.datmusic.data.db.daos.ArtistsDao
import com.kuvandikov.datmusic.data.db.daos.DownloadRequestsDao
import com.kuvandikov.datmusic.data.repos.audio.AudiosRepo
import com.kuvandikov.datmusic.data.repos.playlist.PlaylistsRepo

@HiltAndroidTest
@UninstallModules(DatabaseModule::class)
class RestoreDatmusicBackupTest : BaseTest() {

    @Inject lateinit var database: AppDatabase
    @Inject lateinit var playlistsRepo: PlaylistsRepo
    @Inject lateinit var audiosRepo: AudiosRepo
    @Inject lateinit var artistsDao: ArtistsDao
    @Inject lateinit var albumsDao: AlbumsDao
    @Inject lateinit var downloadRequestsDao: DownloadRequestsDao
    @Inject lateinit var createDatmusicBackup: CreateDatmusicBackup
    @Inject lateinit var restoreDatmusicBackup: RestoreDatmusicBackup

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `restored backup is the same as initial backup after clearing & restoring from initial backup`() = runTest {
        createBackupData(playlistsRepo, audiosRepo, artistsDao, albumsDao, downloadRequestsDao)
        val initialBackup = createDatmusicBackup.execute(Unit)
        database.clearAllTables()

        restoreDatmusicBackup.execute(initialBackup)

        val restoredBackup = createDatmusicBackup.execute(Unit)
        assertThat(restoredBackup)
            .isEqualTo(initialBackup)
    }
}
