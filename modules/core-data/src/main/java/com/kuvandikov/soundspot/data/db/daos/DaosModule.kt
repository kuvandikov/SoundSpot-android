/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.db.daos

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.kuvandikov.data.db.PaginatedEntryDao
import com.kuvandikov.soundspot.data.SoundspotSearchParams
import com.kuvandikov.soundspot.data.db.AppDatabase
import com.kuvandikov.soundspot.domain.entities.Album
import com.kuvandikov.soundspot.domain.entities.Artist
import com.kuvandikov.soundspot.domain.entities.Audio

@InstallIn(SingletonComponent::class)
@Module
class DaosModule {
    @Provides
    fun audiosDao(db: AppDatabase) = db.audiosDao()

    @Provides
    fun audiosDaoBase(db: AppDatabase): PaginatedEntryDao<SoundspotSearchParams, Audio> = db.audiosDao()

    @Provides
    fun audiosFtsDao(db: AppDatabase) = db.audiosFtsDao()

    @Provides
    fun artistsDao(db: AppDatabase) = db.artistsDao()

    @Provides
    fun artistsDaoBase(db: AppDatabase): PaginatedEntryDao<SoundspotSearchParams, Artist> = db.artistsDao()

    @Provides
    fun albumsDao(db: AppDatabase) = db.albumsDao()

    @Provides
    fun albumsDaoBase(db: AppDatabase): PaginatedEntryDao<SoundspotSearchParams, Album> = db.albumsDao()

    @Provides
    fun playlistsDao(db: AppDatabase) = db.playlistsDao()

    @Provides
    fun playlistsWithAudiosDao(db: AppDatabase) = db.playlistsWithAudiosDao()

    @Provides
    fun downloadRequestsDao(db: AppDatabase) = db.downloadRequestsDao()
}
