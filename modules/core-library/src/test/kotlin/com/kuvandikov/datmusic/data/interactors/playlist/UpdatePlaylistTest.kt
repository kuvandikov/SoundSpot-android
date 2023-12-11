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
import com.kuvandikov.datmusic.domain.entities.PLAYLIST_NAME_MAX_LENGTH
import com.kuvandikov.i18n.ValidationErrorBlank
import com.kuvandikov.i18n.ValidationErrorTooLong

@HiltAndroidTest
@UninstallModules(DatabaseModule::class)
class UpdatePlaylistTest : BaseTest() {

    @Inject lateinit var database: AppDatabase
    @Inject lateinit var updatePlaylist: UpdatePlaylist
    @Inject lateinit var repo: PlaylistsRepo

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `updates playlist given playlist`() = runTest {
        val originalPlaylist = SampleData.playlist()
        repo.createPlaylist(originalPlaylist)

        val updatedPlaylist = SampleData.playlist().copy(id = originalPlaylist.id)
        updatePlaylist.execute(updatedPlaylist)

        repo.playlist(originalPlaylist.id).test {
            val playlist = awaitItem()
            assertThat(playlist)
                .isEqualTo(updatedPlaylist.copy(updatedAt = playlist.updatedAt))
        }
    }

    @Test(expected = ValidationErrorBlank::class)
    fun `fails given playlist with empty name`() = runTest {
        val originalPlaylist = SampleData.playlist()
        repo.createPlaylist(originalPlaylist)

        val updatedPlaylist = originalPlaylist.copy(name = "")
        updatePlaylist.execute(updatedPlaylist)
    }

    @Test(expected = ValidationErrorTooLong::class)
    fun `fails given playlist with too long name`() = runTest {
        val originalPlaylist = SampleData.playlist()
        repo.createPlaylist(originalPlaylist)

        val updatedPlaylist = originalPlaylist.copy(name = "a".repeat(PLAYLIST_NAME_MAX_LENGTH + 1))
        updatePlaylist.execute(updatedPlaylist)
    }
}
