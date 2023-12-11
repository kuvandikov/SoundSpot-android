/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data.daos

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import javax.inject.Inject
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import com.kuvandikov.base.testing.BaseTest
import com.kuvandikov.datmusic.data.SampleData
import com.kuvandikov.datmusic.data.db.AppDatabase
import com.kuvandikov.datmusic.data.db.DatabaseModule
import com.kuvandikov.datmusic.domain.entities.Audio

@HiltAndroidTest
@UninstallModules(DatabaseModule::class)
class AudiosFtsDaoTest : BaseTest() {

    @Inject lateinit var database: AppDatabase
    @Inject lateinit var audiosDao: AudiosDao
    @Inject lateinit var playlistsDao: PlaylistsDao
    @Inject lateinit var playlistsWithAudiosDao: PlaylistsWithAudiosDao
    @Inject lateinit var downloadRequestsDao: DownloadRequestsDao
    @Inject lateinit var dao: AudiosFtsDao

    private val testItems = (1..5).map { SampleData.audio() }

    @After
    fun tearDown() {
        database.close()
    }

    private fun Audio.toTestQueries() = listOf(title, artist, album ?: "")

    @Test
    fun `search audios by title, artist, or album`() = runTest {
        val items = testItems
        audiosDao.insertAll(items)

        val randomItem = items.shuffled().first()

        randomItem.toTestQueries().forEach { query ->
            dao.search(query).test {
                assertThat(awaitItem())
                    .isEqualTo(listOf(randomItem))
            }
        }
    }

    @Test
    fun `search playlist items`() = runTest {
        val playlistItems = (1..5).map { SampleData.playlistAudioItem() }
        val items = playlistItems.map { it.playlistAudio }
        playlistsDao.insertAll(playlistItems.map { it.playlist })
        audiosDao.insertAll(playlistItems.map { it.audio })
        playlistsWithAudiosDao.insertAll(items)

        val randomItem = playlistItems.shuffled().first()

        randomItem.audio.toTestQueries().forEach { query ->
            dao.searchPlaylist(randomItem.playlist.id, query).test {
                assertThat(awaitItem().map { it.audio })
                    .isEqualTo(listOf(randomItem.audio))
            }
        }
    }

    @Test
    fun `search download items`() = runTest {
        val items = (1..5).map { SampleData.downloadRequest() }
        downloadRequestsDao.insertAll(items)
        audiosDao.insertAll(items.map { it.audio })

        val randomItem = items.shuffled().first()

        randomItem.audio.toTestQueries().forEach { query ->
            dao.searchDownloads(query).test {
                assertThat(awaitItem().map { it.audio })
                    .isEqualTo(listOf(randomItem.audio))
            }
        }
    }
}
