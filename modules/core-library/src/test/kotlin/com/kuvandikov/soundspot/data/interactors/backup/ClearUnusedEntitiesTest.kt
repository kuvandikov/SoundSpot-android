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
import com.kuvandikov.soundspot.data.SampleData
import com.kuvandikov.soundspot.data.db.AppDatabase
import com.kuvandikov.soundspot.data.db.DatabaseModule
import com.kuvandikov.soundspot.data.db.daos.AlbumsDao
import com.kuvandikov.soundspot.data.db.daos.ArtistsDao
import com.kuvandikov.soundspot.data.db.daos.DownloadRequestsDao
import com.kuvandikov.soundspot.data.repos.audio.AudiosRepo
import com.kuvandikov.soundspot.data.repos.playlist.PlaylistsRepo

@HiltAndroidTest
@UninstallModules(DatabaseModule::class)
class ClearUnusedEntitiesTest : BaseTest() {

    @Inject lateinit var database: AppDatabase
    @Inject lateinit var playlistsRepo: PlaylistsRepo
    @Inject lateinit var audiosRepo: AudiosRepo
    @Inject lateinit var artistsDao: ArtistsDao
    @Inject lateinit var albumsDao: AlbumsDao
    @Inject lateinit var downloadRequestsDao: DownloadRequestsDao
    @Inject lateinit var clearUnusedEntities: ClearUnusedEntities

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `clears unused entities`() = runTest {
        val audioItems = (1..10).map { SampleData.audio() }
            .also { audiosRepo.insertAll(it) }
        val artistItems = (1..10).map { SampleData.artist() }
            .also { artistsDao.insertAll(it) }
        val albumItems = (1..10).map { SampleData.album() }
            .also { albumsDao.insertAll(it) }
        val playlistItemIds = audioItems.shuffled().take(5).map { it.id }
        val playlist = SampleData.playlist()
            .also { playlistsRepo.createPlaylist(it, playlistItemIds) }
        val downloadRequests = audioItems.map { SampleData.downloadRequest(it) }
            .also { audiosRepo.insertAll(it.map { it.audio }) }
            .also { downloadRequestsDao.insertAll(it) }

        clearUnusedEntities.invoke()

        val nondeletedAudioIds = playlistItemIds + downloadRequests.map { it.id }
        assertThat(audiosRepo.entries().first().map { it.id })
            .containsExactlyElementsIn(nondeletedAudioIds)

        assertThat(artistsDao.entries().first())
            .isEmpty()
        assertThat(albumsDao.entries().first())
            .isEmpty()
    }
}
