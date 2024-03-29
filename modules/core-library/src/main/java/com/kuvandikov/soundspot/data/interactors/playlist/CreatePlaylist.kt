/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.interactors.playlist

import android.content.res.Resources
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import com.kuvandikov.base.util.CoroutineDispatchers
import com.kuvandikov.data.ResultInteractor
import com.kuvandikov.soundspot.coreLibrary.R
import com.kuvandikov.soundspot.data.repos.playlist.PlaylistsRepo
import com.kuvandikov.soundspot.domain.entities.AudioIds
import com.kuvandikov.soundspot.domain.entities.Audios
import com.kuvandikov.soundspot.domain.entities.PLAYLIST_NAME_MAX_LENGTH
import com.kuvandikov.soundspot.domain.entities.Playlist

class CreatePlaylist @Inject constructor(
    private val resources: Resources,
    private val repo: PlaylistsRepo,
    private val dispatchers: CoroutineDispatchers,
) : ResultInteractor<CreatePlaylist.Params, Playlist>() {

    data class Params(
        val name: String = "",
        val generateNameIfEmpty: Boolean = true,
        val trimIfTooLong: Boolean = false,
        val audios: Audios = emptyList(),
        val audioIds: AudioIds = emptyList()
    ) {
        fun audioIds() = audios.map { it.id } + audioIds
    }

    override suspend fun doWork(params: Params) = withContext(dispatchers.io) {
        var name = params.name

        if (params.generateNameIfEmpty && name.isBlank()) {
            val playlistCount = repo.count().first() + 1
            name = resources.getString(R.string.playlist_create_generatedTemplate, playlistCount)
        }

        if (name.length > PLAYLIST_NAME_MAX_LENGTH && params.trimIfTooLong)
            name = name.take(PLAYLIST_NAME_MAX_LENGTH)

        val newPlaylist = Playlist(name = name)
        val playlistId = repo.createPlaylist(
            playlist = newPlaylist,
            audioIds = params.audioIds()
        )
        return@withContext repo.playlist(playlistId).first()
    }
}

class CreateOrGetPlaylist @Inject constructor(
    private val repo: PlaylistsRepo,
    private val dispatchers: CoroutineDispatchers,
) : ResultInteractor<CreateOrGetPlaylist.Params, Playlist>() {

    data class Params(
        val name: String = "",
        val audioIds: AudioIds = emptyList(),
        val ignoreExistingAudios: Boolean = true
    )

    override suspend fun doWork(params: Params) = withContext(dispatchers.io) {
        val playlistId = repo.getOrCreatePlaylist(params.name, audioIds = params.audioIds, ignoreExistingAudios = params.ignoreExistingAudios)
        return@withContext repo.playlist(playlistId).first()
    }
}
