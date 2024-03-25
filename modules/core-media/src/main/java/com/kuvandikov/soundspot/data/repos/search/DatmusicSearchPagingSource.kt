/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.repos.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dropbox.android.external.store4.get
import com.kuvandikov.soundspot.data.DATMUSIC_FIRST_PAGE_INDEX
import com.kuvandikov.soundspot.data.SoundspotSearchParams
import com.kuvandikov.soundspot.data.interactors.search.SearchSoundspot
import com.kuvandikov.domain.models.PaginatedEntity

/**
 * Uses [SoundspotSearchStore] to paginate in-memory items that were already fetched via [SearchSoundspot].
 * Not being used anymore, keeping just for reference.
 */
class SoundspotSearchPagingSource<T : PaginatedEntity>(
    private val soundspotSearchStore: SoundspotSearchStore<T>,
    private val searchParams: SoundspotSearchParams
) : PagingSource<Int, T>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: DATMUSIC_FIRST_PAGE_INDEX
        return try {
            val items = soundspotSearchStore.get(searchParams.copy(page = page))

            LoadResult.Page(
                data = items,
                prevKey = if (page == DATMUSIC_FIRST_PAGE_INDEX) null else page - 1,
                nextKey = if (items.isEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
