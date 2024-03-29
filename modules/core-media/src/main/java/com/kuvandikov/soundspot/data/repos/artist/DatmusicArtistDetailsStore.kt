/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.repos.artist

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
import com.kuvandikov.data.LastRequests
import com.kuvandikov.data.PreferencesStore
import com.kuvandikov.soundspot.data.SoundspotArtistParams
import com.kuvandikov.soundspot.data.db.daos.AlbumsDao
import com.kuvandikov.soundspot.data.db.daos.ArtistsDao
import com.kuvandikov.soundspot.data.db.daos.AudiosDao
import com.kuvandikov.soundspot.domain.entities.Artist

typealias SoundspotArtistDetailsStore = Store<SoundspotArtistParams, Artist>

@InstallIn(SingletonComponent::class)
@Module
object SoundspotArtistDetailsStoreModule {

    private suspend fun <T> Result<T>.fetcherDefaults(lastRequests: LastRequests, params: SoundspotArtistParams) = onSuccess {
        if (params.page == 0)
            lastRequests.save(params.toString())
    }.getOrThrow()

    private fun Flow<Artist?>.sourceReaderFilter(lastRequests: LastRequests, params: SoundspotArtistParams) = map { entry ->
        when (entry != null) {
            true -> {
                when {
                    !entry.detailsFetched -> null
                    lastRequests.isExpired(params.toString()) -> null
                    else -> entry
                }
            }
            else -> null
        }
    }

    @Provides
    @Singleton
    fun soundspotArtistDetailsStore(
        artists: SoundspotArtistDataSource,
        dao: ArtistsDao,
        audiosDao: AudiosDao,
        albumsDao: AlbumsDao,
        @Named("artist_details") lastRequests: LastRequests
    ): SoundspotArtistDetailsStore = StoreBuilder.from(
        fetcher = Fetcher.of { params: SoundspotArtistParams ->
            artists(params).map { it.data.artist }.fetcherDefaults(lastRequests, params)
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { params: SoundspotArtistParams ->
                dao.entryNullable(params.id).sourceReaderFilter(lastRequests, params)
            },
            writer = { params, response ->
                dao.withTransaction {
                    val entry = dao.entry(params.id).firstOrNull() ?: response
                    dao.updateOrInsert(entry.copy(audios = response.audios, albums = response.albums, detailsFetched = true))

                    audiosDao.insertMissing(response.audios.mapIndexed { index, audio -> audio.copy(primaryKey = audio.id, searchIndex = index) })
                    albumsDao.insertMissing(response.albums.mapIndexed { index, album -> album.copy(primaryKey = album.id, searchIndex = index) })
                }
            },
            delete = { error("This store doesn't manage deletes") },
            deleteAll = { error("This store doesn't manage deleteAll") },
        )
    ).build()

    @Provides
    @Singleton
    @Named("artist_details")
    fun soundspotArtistDetailsLastRequests(preferences: PreferencesStore) = LastRequests("artist_details", preferences)
}
