/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data.interactors.album

import javax.inject.Inject
import kotlinx.coroutines.withContext
import com.kuvandikov.base.util.CoroutineDispatchers
import com.kuvandikov.data.ResultInteractor
import com.kuvandikov.data.fetch
import com.kuvandikov.datmusic.data.DatmusicAlbumParams
import com.kuvandikov.datmusic.data.repos.album.DatmusicAlbumDetailsStore
import com.kuvandikov.datmusic.domain.entities.Audio

class GetAlbumDetails @Inject constructor(
    private val albumDetailsStore: DatmusicAlbumDetailsStore,
    private val dispatchers: CoroutineDispatchers
) : ResultInteractor<GetAlbumDetails.Params, List<Audio>>() {

    data class Params(val albumParams: DatmusicAlbumParams, val forceRefresh: Boolean = false)

    override suspend fun doWork(params: Params) = withContext(dispatchers.io) {
        albumDetailsStore.fetch(params.albumParams, params.forceRefresh)
    }
}
