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
import com.kuvandikov.base.testing.awaitSingle
import com.kuvandikov.soundspot.data.SampleData
import com.kuvandikov.soundspot.data.db.AppDatabase
import com.kuvandikov.soundspot.data.db.DatabaseModule
import com.kuvandikov.soundspot.data.repos.audio.AudiosRepo
import com.kuvandikov.soundspot.data.repos.playlist.PlaylistsRepo
import com.kuvandikov.soundspot.domain.entities.Playlist

@HiltAndroidTest
@UninstallModules(DatabaseModule::class)
class CreateOrGetPlaylistTest : BaseTest() {

    @Inject lateinit var database: AppDatabase
    @Inject lateinit var getOrCreatePlaylist: CreateOrGetPlaylist
    @Inject lateinit var repo: PlaylistsRepo
    @Inject lateinit var audiosRepo: AudiosRepo

    private val testParams = CreateOrGetPlaylist.Params(name = "Test Name")

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `creates new playlist given new name`() = runTest {
        val params = testParams.copy()

        getOrCreatePlaylist(params).test {
            val playlist = awaitSingle()

            assertThat(playlist.name)
                .isEqualTo(params.name)
            assertThat(repo.getByName(params.name))
                .isEqualTo(playlist)
        }
    }

    @Test
    fun `finds existing playlist given existing name and adds given audioIds`() = runTest {
        val audiosCount = 10
        val audioItems = (1..audiosCount).map { SampleData.audio() }.apply { audiosRepo.insertAll(this) }
        val audioIds = audioItems.map { it.id }
        val params = testParams.copy(audioIds = audioIds)
        val existingPlaylistAudioIds = (1..5).map { SampleData.audio() }.apply { audiosRepo.insertAll(this) }.map { it.id }
        val existingPlaylistId = repo.createPlaylist(Playlist(name = params.name), audioIds = existingPlaylistAudioIds)

        getOrCreatePlaylist(params).test {
            val playlist = awaitSingle()

            assertThat(playlist.name)
                .isEqualTo(params.name)
            assertThat(repo.getByName(params.name)?.id)
                .isEqualTo(existingPlaylistId)
        }

        repo.playlistItems(existingPlaylistId).test {
            val playlistItems = awaitItem()

            assertThat(playlistItems.map { it.audio.id })
                .isEqualTo(existingPlaylistAudioIds + audioIds)

            // Check that playlist item positions are correct
            assertThat(playlistItems.map { it.playlistAudio.position })
                .isEqualTo((0 until (existingPlaylistAudioIds.size + audiosCount)).toList())
        }
    }

    @Test
    fun `finds existing playlist given existing name and ignores given audios when ignoreExisting is true`() = runTest {
        val existingPlaylistAudioIds = (1..5).map { SampleData.audio() }.apply { audiosRepo.insertAll(this) }.map { it.id }
        val params = testParams.copy(audioIds = existingPlaylistAudioIds, ignoreExistingAudios = true)
        val existingPlaylistId = repo.createPlaylist(Playlist(name = params.name), audioIds = existingPlaylistAudioIds)

        getOrCreatePlaylist(params).test {
            val playlist = awaitSingle()

            assertThat(playlist.name)
                .isEqualTo(params.name)
            assertThat(repo.getByName(params.name)?.id)
                .isEqualTo(existingPlaylistId)
        }

        repo.playlistItems(existingPlaylistId).test {
            val playlistItems = awaitItem()

            assertThat(playlistItems.map { it.audio.id })
                .isEqualTo(existingPlaylistAudioIds)

            assertThat(playlistItems.map { it.playlistAudio.position })
                .isEqualTo((existingPlaylistAudioIds.indices).toList())
        }
    }
}
