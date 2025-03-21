/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.interactors.playlist

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
import com.kuvandikov.soundspot.data.repos.audio.AudiosRepo
import com.kuvandikov.soundspot.data.repos.playlist.PlaylistsRepo

@HiltAndroidTest
@UninstallModules(DatabaseModule::class)
class AddToPlaylistTest : BaseTest() {

    @Inject lateinit var database: AppDatabase
    @Inject lateinit var addToPlaylist: AddToPlaylist
    @Inject lateinit var repo: PlaylistsRepo
    @Inject lateinit var audiosRepo: AudiosRepo

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `adds to playlist given audios ids`() = runTest {
        val audioIds = (1..5).map { SampleData.audio() }
            .apply { audiosRepo.insertAll(this) }
            .map { it.id }
        val playlistId = SampleData.playlist().apply { repo.insert(this) }.id
        val params = AddToPlaylist.Params(playlistId, audioIds = audioIds)

        val playlistAudioIds = addToPlaylist.execute(params)
        assertThat(playlistAudioIds.size)
            .isEqualTo(audioIds.size)
        assertThat(playlistAudioIds)
            .isEqualTo(repo.playlistItems(playlistId).first().map { it.playlistAudio.id })
    }

    @Test
    fun `adds to playlist given audios ids but ignores existing given ignoreExisting true`() = runTest {
        val audiosCount = 10
        val existingAudioIdsCount = audiosCount / 2
        val audioIds = (1..audiosCount).map { SampleData.audio() }
            .apply { audiosRepo.insertAll(this) }
            .map { it.id }
        val playlistId = SampleData.playlist().apply { repo.createPlaylist(this, audioIds = audioIds.take(existingAudioIdsCount)) }.id
        val params = AddToPlaylist.Params(playlistId, audioIds = audioIds, ignoreExisting = true)

        val playlistAudioIds = addToPlaylist.execute(params)
        assertThat(playlistAudioIds.size)
            .isEqualTo(audioIds.size - existingAudioIdsCount)
        assertThat(playlistAudioIds)
            .isEqualTo(
                repo.playlistItems(playlistId)
                    .first()
                    .drop(existingAudioIdsCount)
                    .map { it.playlistAudio.id }
            )
    }
}
