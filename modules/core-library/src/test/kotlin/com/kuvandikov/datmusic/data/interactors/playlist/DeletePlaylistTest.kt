/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data.interactors.playlist

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
class DeletePlaylistTest : BaseTest() {

    @Inject lateinit var database: AppDatabase
    @Inject lateinit var deletePlaylist: DeletePlaylist
    @Inject lateinit var repo: PlaylistsRepo

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `deletes playlist given playlist id`() = runTest {
        val playlistId = repo.createPlaylist(SampleData.playlist())

        deletePlaylist.execute(playlistId)

        assertThat(repo.exists(playlistId))
            .isFalse()
    }
}
