/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.interactors.album

import javax.inject.Inject
import kotlinx.coroutines.withContext
import com.kuvandikov.base.util.CoroutineDispatchers
import com.kuvandikov.data.ResultInteractor
import com.kuvandikov.data.fetch
import com.kuvandikov.soundspot.data.SoundspotAlbumParams
import com.kuvandikov.soundspot.data.repos.album.SoundspotAlbumDetailsStore
import com.kuvandikov.soundspot.domain.entities.Audio

class GetAlbumDetails @Inject constructor(
    private val albumDetailsStore: SoundspotAlbumDetailsStore,
    private val dispatchers: CoroutineDispatchers
) : ResultInteractor<GetAlbumDetails.Params, List<Audio>>() {

    data class Params(val albumParams: SoundspotAlbumParams, val forceRefresh: Boolean = false)

    override suspend fun doWork(params: Params) = withContext(dispatchers.io) {
        albumDetailsStore.fetch(params.albumParams, params.forceRefresh)
    }
}
