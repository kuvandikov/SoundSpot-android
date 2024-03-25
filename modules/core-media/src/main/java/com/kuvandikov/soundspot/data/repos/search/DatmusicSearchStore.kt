/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.repos.search

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreBuilder
import com.kuvandikov.data.LastRequests
import com.kuvandikov.data.PreferencesStore
import com.kuvandikov.soundspot.data.SoundspotSearchParams
import com.kuvandikov.soundspot.data.db.daos.AlbumsDao
import com.kuvandikov.soundspot.data.db.daos.ArtistsDao
import com.kuvandikov.soundspot.data.db.daos.AudiosDao
import com.kuvandikov.soundspot.domain.entities.Album
import com.kuvandikov.soundspot.domain.entities.Artist
import com.kuvandikov.soundspot.domain.entities.Audio
import com.kuvandikov.soundspot.domain.models.errors.requireNonEmpty
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.threeten.bp.Duration
import javax.inject.Named
import javax.inject.Singleton

typealias SoundspotSearchStore<T> = Store<SoundspotSearchParams, List<T>>

@InstallIn(SingletonComponent::class)
@Module
object SoundspotSearchStoreModule {

    private fun searchPrimaryKey(id: Any, params: SoundspotSearchParams) = "${id}_${params.toString().hashCode()}"

    private suspend fun <T> Result<List<T>>.fetcherDefaults(lastRequests: LastRequests, params: SoundspotSearchParams) = onSuccess {
        lastRequests.save(params = params.toString() + params.page)
    }.requireNonEmpty()

    private fun <T> Flow<List<T>>.sourceReaderFilter(lastRequests: LastRequests, params: SoundspotSearchParams) = map { entries ->
        when {
            entries.isEmpty() -> null // because Store only treats nulls as no-value
            lastRequests.isExpired(params = params.toString() + params.page) -> null // this source is invalid if it's expired
            else -> entries
        }
    }

    @Provides
    @Singleton
    fun soundspotSearchAudioStore(
        search: SoundspotSearchDataSource,
        dao: AudiosDao,
        @Named("audios") lastRequests: LastRequests
    ): SoundspotSearchStore<Audio> = StoreBuilder.from(
        fetcher = Fetcher.of { params: SoundspotSearchParams ->
            search(params).map { it.data.audios + it.hits }
                .fetcherDefaults(lastRequests, params)
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { params -> dao.entries(params, params.page).sourceReaderFilter(lastRequests, params) },
            writer = { params, response ->
                dao.withTransaction {
                    val entries = response.mapIndexed { index, it ->
                        it.copy(
                            params = params.toString(),
                            page = params.page,
                            searchIndex = index,
                            primaryKey = searchPrimaryKey(it.id, params),
                        )
                    }
                    dao.update(params, params.page, entries)
                }
            },
            delete = dao::delete,
            deleteAll = dao::deleteAll
        )
    ).build()

    @Provides
    @Singleton
    fun soundspotSearchArtistsStore(
        search: SoundspotSearchDataSource,
        dao: ArtistsDao,
        @Named("artists") lastRequests: LastRequests
    ): SoundspotSearchStore<Artist> = StoreBuilder.from(
        fetcher = Fetcher.of { params: SoundspotSearchParams -> search(params).map { it.data.artists }.fetcherDefaults(lastRequests, params) },
        sourceOfTruth = SourceOfTruth.of(
            reader = { params -> dao.entries(params, params.page).sourceReaderFilter(lastRequests, params) },
            writer = { params, response ->
                dao.withTransaction {
                    val entries =
                        response.mapIndexed { index, it ->
                            it.copy(
                                params = params.toString(),
                                page = params.page,
                                searchIndex = index,
                                primaryKey = searchPrimaryKey(it.id, params),
                            )
                        }
                    dao.update(params, params.page, entries)
                }
            },
            delete = dao::delete,
            deleteAll = dao::deleteAll
        )
    ).build()

    @Provides
    @Singleton
    fun soundspotSearchAlbumsStore(
        search: SoundspotSearchDataSource,
        dao: AlbumsDao,
        @Named("albums") lastRequests: LastRequests
    ): SoundspotSearchStore<Album> = StoreBuilder.from(
        fetcher = Fetcher.of { params: SoundspotSearchParams -> search(params).map { it.data.albums }.fetcherDefaults(lastRequests, params) },
        sourceOfTruth = SourceOfTruth.of(
            reader = { params -> dao.entries(params, params.page).sourceReaderFilter(lastRequests, params) },
            writer = { params, response ->
                dao.withTransaction {
                    val entries =
                        response.mapIndexed { index, it ->
                            it.copy(
                                params = params.toString(),
                                page = params.page,
                                searchIndex = index,
                                primaryKey = searchPrimaryKey(it.id, params),
                            )
                        }
                    dao.update(params, params.page, entries)
                }
            },
            delete = dao::delete,
            deleteAll = dao::deleteAll
        )
    ).build()

    @Provides
    @Singleton
    @Named("audios")
    fun searchAudiosLastRequests(preferences: PreferencesStore) = LastRequests("search_audios", preferences)

    @Provides
    @Singleton
    @Named("artists")
    fun searchArtistsLastRequests(preferences: PreferencesStore) = LastRequests("search_artists", preferences, Duration.ofDays(7))

    @Provides
    @Singleton
    @Named("albums")
    fun searchAlbumsLastRequests(preferences: PreferencesStore) = LastRequests("search_albums", preferences, Duration.ofDays(7))
}
