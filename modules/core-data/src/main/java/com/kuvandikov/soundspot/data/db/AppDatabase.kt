/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kuvandikov.soundspot.data.db.daos.AlbumsDao
import com.kuvandikov.soundspot.data.db.daos.ArtistsDao
import com.kuvandikov.soundspot.data.db.daos.AudiosDao
import com.kuvandikov.soundspot.data.db.daos.AudiosFtsDao
import com.kuvandikov.soundspot.data.db.daos.DownloadRequestsDao
import com.kuvandikov.soundspot.data.db.daos.PlaylistsDao
import com.kuvandikov.soundspot.data.db.daos.PlaylistsWithAudiosDao
import com.kuvandikov.soundspot.domain.entities.Album
import com.kuvandikov.soundspot.domain.entities.Artist
import com.kuvandikov.soundspot.domain.entities.Audio
import com.kuvandikov.soundspot.domain.entities.AudioFts
import com.kuvandikov.soundspot.domain.entities.DownloadRequest
import com.kuvandikov.soundspot.domain.entities.Playlist
import com.kuvandikov.soundspot.domain.entities.PlaylistAudio
import com.kuvandikov.domain.models.BaseTypeConverters

const val SQLITE_MAX_VARIABLES = 900

@Database(
    version = 1,
    entities = [
        Audio::class,
        AudioFts::class,
        Artist::class,
        Album::class,
        DownloadRequest::class,
        Playlist::class,
        PlaylistAudio::class,
    ]
)
@TypeConverters(BaseTypeConverters::class, AppTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun audiosDao(): AudiosDao
    abstract fun audiosFtsDao(): AudiosFtsDao

    abstract fun artistsDao(): ArtistsDao
    abstract fun albumsDao(): AlbumsDao

    abstract fun playlistsDao(): PlaylistsDao
    abstract fun playlistsWithAudiosDao(): PlaylistsWithAudiosDao

    abstract fun downloadRequestsDao(): DownloadRequestsDao
}
