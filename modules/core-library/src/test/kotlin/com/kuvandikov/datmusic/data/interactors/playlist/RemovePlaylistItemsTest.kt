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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import com.kuvandikov.base.testing.BaseTest
import com.kuvandikov.datmusic.data.SampleData
import com.kuvandikov.datmusic.data.db.AppDatabase
import com.kuvandikov.datmusic.data.db.DatabaseModule
import com.kuvandikov.datmusic.data.repos.audio.AudiosRepo
import com.kuvandikov.datmusic.data.repos.playlist.PlaylistsRepo

@HiltAndroidTest
@UninstallModules(DatabaseModule::class)
class RemovePlaylistItemsTest : BaseTest() {

    @Inject lateinit var database: AppDatabase
    @Inject lateinit var removePlaylistItems: RemovePlaylistItems
    @Inject lateinit var repo: PlaylistsRepo
    @Inject lateinit var audiosRepo: AudiosRepo

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `removes playlist items given playlist item ids`() = runTest {
        val audioIds = (1..5).map { SampleData.audio() }.also { audiosRepo.insertAll(it) }.map { it.id }
        val id = repo.createPlaylist(SampleData.playlist(), audioIds)

        val playlistItems = repo.playlistItems(id).first().shuffled()
        val playlistItemsToRemove = playlistItems.take(2)
        val removedItemsCount = removePlaylistItems.execute(playlistItemsToRemove.map { it.playlistAudio.id })

        assertThat(removedItemsCount)
            .isEqualTo(playlistItemsToRemove.size)

        repo.playlistItems(id).test {
            assertThat(awaitItem())
                .containsExactlyElementsIn(playlistItems.subtract(playlistItemsToRemove))
        }
    }
}
