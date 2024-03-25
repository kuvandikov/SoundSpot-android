/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.repos.audio

import javax.inject.Inject
import timber.log.Timber
import com.kuvandikov.base.util.CoroutineDispatchers
import com.kuvandikov.data.db.RoomRepo
import com.kuvandikov.soundspot.data.SoundspotSearchParams
import com.kuvandikov.soundspot.data.db.daos.AudiosDao
import com.kuvandikov.soundspot.data.db.daos.DownloadRequestsDao
import com.kuvandikov.soundspot.domain.entities.Audio
import com.kuvandikov.soundspot.domain.entities.AudioId
import com.kuvandikov.soundspot.domain.entities.AudioIds
import com.kuvandikov.soundspot.domain.entities.Audios
import com.kuvandikov.soundspot.domain.entities.DownloadRequest

enum class AudioSaveType {
    Download, Playlist;

    fun toAudioParams() = "save_type=${toString()}"
}

class AudiosRepo @Inject constructor(
    private val dispatchers: CoroutineDispatchers,
    private val dao: AudiosDao,
    private val downloadsRequestsDao: DownloadRequestsDao,
) : RoomRepo<AudioId, Audio>(dao, dispatchers) {

    suspend fun entriesByParams(params: SoundspotSearchParams) = dao.entries(params)
    suspend fun audiosById(ids: AudioIds) = dao.audiosById(ids)

    suspend fun saveAudiosById(type: AudioSaveType, audioIds: AudioIds) = saveAudios(type, audiosById(audioIds))

    suspend fun saveAudios(type: AudioSaveType, audios: Audios) = saveAudios(type, *audios.toTypedArray())

    suspend fun saveAudios(type: AudioSaveType, vararg audios: Audio): Int {
        val mapped = audios.map { it.copy(primaryKey = it.id, params = type.toAudioParams()) }
        return insertAll(mapped).size
    }

    private suspend fun findAudioDownloadsById(ids: AudioIds) = downloadsRequestsDao.getByIdAndType(ids, DownloadRequest.Type.Audio)

    suspend fun find(audioId: String): Audio? = find(listOf(audioId)).firstOrNull()

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun find(ids: AudioIds): List<Audio> {
        val audios = audiosById(ids).map { it.id to it }.toMap()
        val downloads = findAudioDownloadsById(ids).map { it.audio.id to it.audio }.toMap()
        return buildList {
            ids.forEach { id ->
                val audio = audios[id] ?: downloads[id]
                if (audio == null) {
                    Timber.e("Couldn't find audio by id: $id")
                } else add(audio)
            }
        }
    }

    /**
     * Finds missing audio ids from given ids.
     * Tries to recover missing ids from downloads via [findAudioDownloadsById].
     */
    suspend fun findMissingIds(ids: AudioIds): AudioIds {
        val existingIds = audiosById(ids).map { it.id }.toSet()
        val missingIds = ids.filterNot { existingIds.contains(it) }

        val recoveredAudios = findAudioDownloadsById(missingIds).map { it.audio }
        val recoveredAudioIds = recoveredAudios.map { it.id }.toSet()
        insertAll(recoveredAudios)

        return missingIds.filterNot { recoveredAudioIds.contains(it) }
    }
}
