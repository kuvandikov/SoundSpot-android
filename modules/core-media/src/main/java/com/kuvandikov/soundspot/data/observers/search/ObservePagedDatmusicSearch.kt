/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.observers.search

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import com.kuvandikov.data.PaginatedEntryRemoteMediator
import com.kuvandikov.data.PagingInteractor
import com.kuvandikov.data.db.PaginatedEntryDao
import com.kuvandikov.soundspot.data.SoundspotSearchParams
import com.kuvandikov.soundspot.data.interactors.search.SearchSoundspot
import com.kuvandikov.domain.models.PaginatedEntity

@OptIn(ExperimentalPagingApi::class)
class ObservePagedSoundspotSearch<T : PaginatedEntity> @Inject constructor(
    private val searchSoundspot: SearchSoundspot<T>,
    private val dao: PaginatedEntryDao<SoundspotSearchParams, T>
) : PagingInteractor<ObservePagedSoundspotSearch.Params<T>, T>() {

    override fun createObservable(
        params: Params<T>
    ): Flow<PagingData<T>> {
        return Pager(
            config = params.pagingConfig,
            remoteMediator = PaginatedEntryRemoteMediator { page, refreshing ->
                try {
                    searchSoundspot.execute(
                        SearchSoundspot.Params(searchParams = params.searchParams.copy(page = page), forceRefresh = refreshing)
                    )
                } catch (error: Exception) {
                    onError(error)
                    throw error
                }
            },
            pagingSourceFactory = { dao.entriesPagingSource(params.searchParams) }
        ).flow
    }

    data class Params<T : PaginatedEntity>(
        val searchParams: SoundspotSearchParams,
        override val pagingConfig: PagingConfig = DEFAULT_PAGING_CONFIG,
    ) : Parameters<T>
}
