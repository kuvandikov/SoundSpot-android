/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.interactors.search

import javax.inject.Inject
import kotlinx.coroutines.withContext
import com.kuvandikov.base.util.CoroutineDispatchers
import com.kuvandikov.data.Interactor
import com.kuvandikov.data.fetch
import com.kuvandikov.soundspot.data.SoundspotSearchParams
import com.kuvandikov.soundspot.data.repos.search.SoundspotSearchStore
import com.kuvandikov.domain.models.BaseEntity

class SearchSoundspot<T : BaseEntity> @Inject constructor(
    private val soundspotSearchStore: SoundspotSearchStore<T>,
    private val dispatchers: CoroutineDispatchers
) : Interactor<SearchSoundspot.Params>() {

    data class Params(val searchParams: SoundspotSearchParams, val forceRefresh: Boolean = false)

    override suspend fun doWork(params: Params) {
        withContext(dispatchers.io) {
            soundspotSearchStore.fetch(params.searchParams, params.forceRefresh)
        }
    }
}
