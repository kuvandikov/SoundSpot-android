/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.interactors.playlist

import android.net.Uri
import app.cash.turbine.test
import coil.ImageLoader
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import javax.inject.Inject
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import com.kuvandikov.base.testing.BaseTest
import com.kuvandikov.soundspot.data.SampleData
import com.kuvandikov.soundspot.data.db.AppDatabase
import com.kuvandikov.soundspot.data.db.DatabaseModule
import com.kuvandikov.soundspot.data.repos.playlist.ArtworkImageFileType
import com.kuvandikov.soundspot.data.repos.playlist.PlaylistsRepo

@HiltAndroidTest
@UninstallModules(DatabaseModule::class)
class SetCustomPlaylistArtworkTest : BaseTest() {

    @Inject lateinit var database: AppDatabase
    @Inject lateinit var setCustomPlaylistArtwork: SetCustomPlaylistArtwork
    @Inject lateinit var repo: PlaylistsRepo
    @Inject lateinit var imageLoader: ImageLoader

    private val testParams = SetCustomPlaylistArtwork.Params(uri = Uri.parse("test"), playlistId = -1)

    @After
    fun tearDown() {
        database.close()
        imageLoader.shutdown()
    }

    @Test
    fun `sets custom playlist artwork given uri`() = runTest {
        val playlistId = repo.createPlaylist(SampleData.playlist())
        val params = testParams.copy(playlistId = playlistId)

        setCustomPlaylistArtwork.execute(params)

        repo.playlist(playlistId).test {
            val playlist = awaitItem()
            assertThat(playlist.artworkPath)
                .isNotEmpty()
            assertThat(playlist.artworkPath)
                .contains(ArtworkImageFileType.PLAYLIST_USER_SET.prefix)
        }
    }
}
