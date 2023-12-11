/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.playback

import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import com.kuvandikov.datmusic.data.DatmusicSearchParams
import com.kuvandikov.datmusic.data.DatmusicSearchParams.Companion.withTypes
import com.kuvandikov.datmusic.data.db.daos.AlbumsDao
import com.kuvandikov.datmusic.data.db.daos.ArtistsDao
import com.kuvandikov.datmusic.data.repos.audio.AudiosRepo
import com.kuvandikov.datmusic.data.repos.playlist.PlaylistsRepo
import com.kuvandikov.datmusic.domain.entities.Audio
import com.kuvandikov.datmusic.domain.entities.asAudios
import com.kuvandikov.datmusic.downloader.observers.ObserveDownloads
import com.kuvandikov.datmusic.playback.models.MEDIA_TYPE_ALBUM
import com.kuvandikov.datmusic.playback.models.MEDIA_TYPE_ARTIST
import com.kuvandikov.datmusic.playback.models.MEDIA_TYPE_AUDIO
import com.kuvandikov.datmusic.playback.models.MEDIA_TYPE_AUDIO_FLACS_QUERY
import com.kuvandikov.datmusic.playback.models.MEDIA_TYPE_AUDIO_MINERVA_QUERY
import com.kuvandikov.datmusic.playback.models.MEDIA_TYPE_AUDIO_QUERY
import com.kuvandikov.datmusic.playback.models.MEDIA_TYPE_DOWNLOADS
import com.kuvandikov.datmusic.playback.models.MEDIA_TYPE_PLAYLIST
import com.kuvandikov.datmusic.playback.models.MediaId
import com.kuvandikov.datmusic.playback.models.QueueTitle

class MediaQueueBuilder @Inject constructor(
    private val audiosRepo: AudiosRepo,
    private val artistsDao: ArtistsDao,
    private val albumsDao: AlbumsDao,
    private val playlistsRepo: PlaylistsRepo,
    private val downloads: ObserveDownloads,
) {
    suspend fun buildAudioList(source: MediaId): List<Audio> = with(source) {
        when (type) {
            MEDIA_TYPE_AUDIO -> listOfNotNull(audiosRepo.entry(value).firstOrNull())
            MEDIA_TYPE_ALBUM -> albumsDao.entry(value).firstOrNull()?.audios
            MEDIA_TYPE_ARTIST -> artistsDao.entry(value).firstOrNull()?.audios
            MEDIA_TYPE_PLAYLIST -> playlistsRepo.playlistItems(value.toLong()).firstOrNull()?.asAudios()
            MEDIA_TYPE_DOWNLOADS -> downloads.execute(ObserveDownloads.Params()).audios.map { it.audio }
            MEDIA_TYPE_AUDIO_QUERY, MEDIA_TYPE_AUDIO_MINERVA_QUERY, MEDIA_TYPE_AUDIO_FLACS_QUERY -> {
                val params = DatmusicSearchParams(value).run {
                    when (type) {
                        MEDIA_TYPE_AUDIO_MINERVA_QUERY -> withTypes(DatmusicSearchParams.BackendType.MINERVA)
                        MEDIA_TYPE_AUDIO_FLACS_QUERY -> withTypes(DatmusicSearchParams.BackendType.FLACS)
                        else -> this
                    }
                }
                audiosRepo.entriesByParams(params).first()
            }
            else -> null
        }.orEmpty()
    }

    suspend fun buildQueueTitle(source: MediaId): QueueTitle = with(source) {
        when (type) {
            MEDIA_TYPE_AUDIO -> QueueTitle(this, QueueTitle.Type.AUDIO, audiosRepo.entry(value).firstOrNull()?.title)
            MEDIA_TYPE_ARTIST -> QueueTitle(this, QueueTitle.Type.ARTIST, artistsDao.entry(value).firstOrNull()?.name)
            MEDIA_TYPE_ALBUM -> QueueTitle(this, QueueTitle.Type.ALBUM, albumsDao.entry(value).firstOrNull()?.title)
            MEDIA_TYPE_PLAYLIST -> QueueTitle(this, QueueTitle.Type.PLAYLIST, playlistsRepo.playlist(value.toLong()).firstOrNull()?.name)
            MEDIA_TYPE_DOWNLOADS -> QueueTitle(this, QueueTitle.Type.DOWNLOADS)
            MEDIA_TYPE_AUDIO_QUERY, MEDIA_TYPE_AUDIO_MINERVA_QUERY, MEDIA_TYPE_AUDIO_FLACS_QUERY -> QueueTitle(this, QueueTitle.Type.SEARCH, value)
            else -> QueueTitle()
        }
    }
}
