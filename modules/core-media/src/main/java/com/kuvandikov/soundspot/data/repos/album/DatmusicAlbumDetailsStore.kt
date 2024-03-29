/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.repos.album

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import timber.log.Timber
import com.kuvandikov.data.LastRequests
import com.kuvandikov.data.PreferencesStore
import com.kuvandikov.soundspot.data.SoundspotAlbumParams
import com.kuvandikov.soundspot.data.db.daos.AlbumsDao
import com.kuvandikov.soundspot.data.db.daos.AudiosDao
import com.kuvandikov.soundspot.domain.entities.Album
import com.kuvandikov.soundspot.domain.entities.Audio

typealias SoundspotAlbumDetailsStore = Store<SoundspotAlbumParams, List<Audio>>

@InstallIn(SingletonComponent::class)
@Module
object SoundspotAlbumDetailsStoreModule {

    private suspend fun <T> Result<T>.fetcherDefaults(lastRequests: LastRequests, params: SoundspotAlbumParams) =
        onSuccess { lastRequests.save(params.toString()) }
            .onFailure { Timber.e(it) }
            .getOrThrow()

    private fun Flow<Album?>.sourceReaderFilter(lastRequests: LastRequests, params: SoundspotAlbumParams) = map { entry ->
        when (entry != null) {
            true -> {
                when {
                    !entry.detailsFetched -> null
                    lastRequests.isExpired(params.toString()) -> null
                    else -> entry.audios
                }
            }
            else -> null
        }
    }

    @Provides
    @Singleton
    fun soundspotAlbumDetailsStore(
        albums: SoundspotAlbumDataSource,
        dao: AlbumsDao,
        audiosDao: AudiosDao,
        @Named("album_details") lastRequests: LastRequests
    ): SoundspotAlbumDetailsStore = StoreBuilder.from(
        fetcher = Fetcher.of { params: SoundspotAlbumParams ->
            albums(params).map { it.data.album to it.data.audios }.fetcherDefaults(lastRequests, params)
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { params: SoundspotAlbumParams -> dao.entry(params.id.toString()).sourceReaderFilter(lastRequests, params) },
            writer = { params, (newEntry, audios) ->
                dao.withTransaction {
                    val entry = dao.entry(params.id.toString()).firstOrNull() ?: newEntry
                    dao.updateOrInsert(
                        entry.copy(
                            audios = audios,
                            detailsFetched = true,
                            year = newEntry.year,
                        )
                    )
                    audiosDao.insertMissing(audios.mapIndexed { index, audio -> audio.copy(primaryKey = audio.id, searchIndex = index) })
                }
            },
            delete = { error("This store doesn't manage deletes") },
            deleteAll = { error("This store doesn't manage deleteAll") },
        )
    ).build()

    @Provides
    @Singleton
    @Named("album_details")
    fun soundspotAlbumDetailsLastRequests(preferences: PreferencesStore) = LastRequests("album_details", preferences)
}
