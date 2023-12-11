/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data.interactors.backup

import javax.inject.Inject
import timber.log.Timber
import com.kuvandikov.datmusic.data.db.daos.AlbumsDao
import com.kuvandikov.datmusic.data.db.daos.ArtistsDao
import com.kuvandikov.datmusic.data.db.daos.AudiosDao
import com.kuvandikov.datmusic.data.db.daos.DownloadRequestsDao
import com.kuvandikov.datmusic.data.db.daos.PlaylistsWithAudiosDao
import com.kuvandikov.datmusic.domain.entities.AudioIds
import com.kuvandikov.datmusic.domain.entities.DownloadRequest

class ClearUnusedEntities @Inject constructor(
    private val audiosDao: AudiosDao,
    private val artistsDao: ArtistsDao,
    private val albumsDao: AlbumsDao,
    private val downloadsRequestsDao: DownloadRequestsDao,
    private val playlistWithAudios: PlaylistsWithAudiosDao,
) {
    data class Result(
        val deletedAudiosCount: Int,
        val deletedArtistsCount: Int,
        val deletedAlbumsCount: Int,
        val whitelistedAudioIds: AudioIds,
    )
    /**
     * Delete all audios except the ones in downloads or playlists and delete all artists/albums.
     */
    suspend operator fun invoke(): Result {
        val downloadRequestAudios = downloadsRequestsDao.getByType(DownloadRequest.Type.Audio)
        val downloadedAudioIds = downloadRequestAudios.map { it.id }
        val audioIdsInPlaylists = playlistWithAudios.distinctAudios()

        val audioIds = downloadedAudioIds + audioIdsInPlaylists

        val deletedAudios = audiosDao.deleteExcept(audioIds)
        val deletedArtists = artistsDao.deleteAll()
        val deletedAlbums = albumsDao.deleteAll()
        val result = Result(deletedAudios, deletedArtists, deletedAlbums, audioIds)
        Timber.i(result.toString())
        return result
    }
}
