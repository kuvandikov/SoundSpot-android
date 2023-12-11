/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data.interactors.search

import javax.inject.Inject
import kotlinx.coroutines.withContext
import com.kuvandikov.base.util.CoroutineDispatchers
import com.kuvandikov.data.Interactor
import com.kuvandikov.data.fetch
import com.kuvandikov.datmusic.data.DatmusicSearchParams
import com.kuvandikov.datmusic.data.repos.search.DatmusicSearchStore
import com.kuvandikov.domain.models.BaseEntity

class SearchDatmusic<T : BaseEntity> @Inject constructor(
    private val datmusicSearchStore: DatmusicSearchStore<T>,
    private val dispatchers: CoroutineDispatchers
) : Interactor<SearchDatmusic.Params>() {

    data class Params(val searchParams: DatmusicSearchParams, val forceRefresh: Boolean = false)

    override suspend fun doWork(params: Params) {
        withContext(dispatchers.io) {
            datmusicSearchStore.fetch(params.searchParams, params.forceRefresh)
        }
    }
}
