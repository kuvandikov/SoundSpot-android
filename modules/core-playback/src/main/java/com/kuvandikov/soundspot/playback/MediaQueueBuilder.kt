/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.playback

import com.kuvandikov.soundspot.data.SoundspotSearchParams
import com.kuvandikov.soundspot.data.db.daos.AlbumsDao
import com.kuvandikov.soundspot.data.db.daos.ArtistsDao
import com.kuvandikov.soundspot.data.repos.audio.AudiosRepo
import com.kuvandikov.soundspot.data.repos.playlist.PlaylistsRepo
import com.kuvandikov.soundspot.domain.entities.Audio
import com.kuvandikov.soundspot.domain.entities.asAudios
import com.kuvandikov.soundspot.downloader.observers.ObserveDownloads
import com.kuvandikov.soundspot.playback.models.MEDIA_TYPE_ALBUM
import com.kuvandikov.soundspot.playback.models.MEDIA_TYPE_ARTIST
import com.kuvandikov.soundspot.playback.models.MEDIA_TYPE_AUDIO
import com.kuvandikov.soundspot.playback.models.MEDIA_TYPE_AUDIO_QUERY
import com.kuvandikov.soundspot.playback.models.MEDIA_TYPE_DOWNLOADS
import com.kuvandikov.soundspot.playback.models.MEDIA_TYPE_PLAYLIST
import com.kuvandikov.soundspot.playback.models.MediaId
import com.kuvandikov.soundspot.playback.models.QueueTitle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

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
            MEDIA_TYPE_AUDIO_QUERY -> {
                val params = SoundspotSearchParams(value)
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
            MEDIA_TYPE_AUDIO_QUERY -> QueueTitle(this, QueueTitle.Type.SEARCH, value)
            else -> QueueTitle()
        }
    }
}
