/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.domain.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.kuvandikov.coreDomain.R
import com.kuvandikov.domain.models.JSON
import com.kuvandikov.i18n.UiMessage
import com.kuvandikov.i18n.ValidationError

const val BACKUP_CURRENT_VERSION = 1

data class SoundspotBackupVersionValidation(val version: Int) : ValidationError(
    UiMessage.Resource(
        R.string.settings_database_restore_NonMatchingVersion,
        listOf(BACKUP_CURRENT_VERSION, version)
    )
) {
    override fun isValid() = version == BACKUP_CURRENT_VERSION
}

@Serializable
data class SoundspotBackupData(
    val audios: Audios,
    val playlists: Playlists,
    val playlistAudios: PlaylistAudios,

    @SerialName("backup_version")
    private val version: Int,
) {

    fun checkVersion() {
        SoundspotBackupVersionValidation(version).validate()
    }

    fun toJson() = JSON.encodeToString(serializer(), this)

    companion object {
        fun fromJson(json: String) = JSON.decodeFromString(serializer(), json)

        fun create(
            audios: Audios,
            playlists: Playlists,
            playlistAudios: PlaylistAudios
        ) = SoundspotBackupData(audios, playlists, playlistAudios, BACKUP_CURRENT_VERSION)
    }
}
