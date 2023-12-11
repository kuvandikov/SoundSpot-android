/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data.interactors.artist

import javax.inject.Inject
import kotlinx.coroutines.withContext
import com.kuvandikov.base.util.CoroutineDispatchers
import com.kuvandikov.data.ResultInteractor
import com.kuvandikov.data.fetch
import com.kuvandikov.datmusic.data.DatmusicArtistParams
import com.kuvandikov.datmusic.data.repos.artist.DatmusicArtistDetailsStore
import com.kuvandikov.datmusic.domain.entities.Artist

class GetArtistDetails @Inject constructor(
    private val artistDetailsStore: DatmusicArtistDetailsStore,
    private val dispatchers: CoroutineDispatchers
) : ResultInteractor<GetArtistDetails.Params, Artist>() {

    data class Params(val artistParams: DatmusicArtistParams, val forceRefresh: Boolean = false)

    override suspend fun doWork(params: Params) = withContext(dispatchers.network) {
        artistDetailsStore.fetch(params.artistParams, params.forceRefresh)
    }
}
