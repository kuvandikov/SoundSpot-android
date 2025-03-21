/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.interactors.artist

import javax.inject.Inject
import kotlinx.coroutines.withContext
import com.kuvandikov.base.util.CoroutineDispatchers
import com.kuvandikov.data.ResultInteractor
import com.kuvandikov.data.fetch
import com.kuvandikov.soundspot.data.SoundspotArtistParams
import com.kuvandikov.soundspot.data.repos.artist.SoundspotArtistDetailsStore
import com.kuvandikov.soundspot.domain.entities.Artist

class GetArtistDetails @Inject constructor(
    private val artistDetailsStore: SoundspotArtistDetailsStore,
    private val dispatchers: CoroutineDispatchers
) : ResultInteractor<GetArtistDetails.Params, Artist>() {

    data class Params(val artistParams: SoundspotArtistParams, val forceRefresh: Boolean = false)

    override suspend fun doWork(params: Params) = withContext(dispatchers.network) {
        artistDetailsStore.fetch(params.artistParams, params.forceRefresh)
    }
}
