/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.interactors.playlist

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import javax.inject.Inject
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import com.kuvandikov.base.testing.BaseTest
import com.kuvandikov.base.util.extensions.swap
import com.kuvandikov.soundspot.data.SampleData
import com.kuvandikov.soundspot.data.db.AppDatabase
import com.kuvandikov.soundspot.data.db.DatabaseModule
import com.kuvandikov.soundspot.data.repos.audio.AudiosRepo
import com.kuvandikov.soundspot.data.repos.playlist.PlaylistsRepo

@HiltAndroidTest
@UninstallModules(DatabaseModule::class)
class ReorderPlaylistTest : BaseTest() {

    @Inject lateinit var database: AppDatabase
    @Inject lateinit var reorderPlaylist: ReorderPlaylist
    @Inject lateinit var repo: PlaylistsRepo
    @Inject lateinit var audiosRepo: AudiosRepo

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `swaps position of playlist items given playlist id and from, to`() = runTest {
        val playlist = SampleData.playlist()
        val audioIds = (1..5).map { SampleData.audio() }
            .apply { audiosRepo.insertAll(this) }
            .map { it.id }
        repo.createPlaylist(playlist, audioIds = audioIds)

        val params = ReorderPlaylist.Params(playlist.id, 0, 4)
        reorderPlaylist.execute(params)

        repo.playlistItems(playlist.id).test {
            assertThat(awaitItem().map { it.audio.id })
                .isEqualTo(audioIds.swap(0, 4))
        }
    }
}
