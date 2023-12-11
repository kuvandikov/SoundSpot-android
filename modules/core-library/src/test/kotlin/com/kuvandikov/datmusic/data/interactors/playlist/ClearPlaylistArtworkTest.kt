/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data.interactors.playlist

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
import com.kuvandikov.datmusic.data.repos.playlist.PlaylistsRepo

@HiltAndroidTest
@UninstallModules(DatabaseModule::class)
class ClearPlaylistArtworkTest : BaseTest() {

    @Inject lateinit var database: AppDatabase
    @Inject lateinit var clearPlaylistArtwork: ClearPlaylistArtwork
    @Inject lateinit var repo: PlaylistsRepo

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `clears playlist artwork given playlist id`() = runTest {
        val playlistId = repo.createPlaylist(SampleData.playlist().copy(artworkPath = "some-artwork"))

        clearPlaylistArtwork.execute(playlistId)

        repo.playlist(playlistId).test {
            val playlist = awaitItem()
            assertThat(playlist.artworkPath)
                .isNull()
            assertThat(playlist.artworkSource)
                .isNull()
        }
    }
}
