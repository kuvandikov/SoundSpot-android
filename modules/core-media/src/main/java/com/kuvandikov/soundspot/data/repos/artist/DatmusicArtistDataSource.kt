/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.repos.artist

import javax.inject.Inject
import com.kuvandikov.base.util.CoroutineDispatchers
import com.kuvandikov.data.resultApiCall
import com.kuvandikov.soundspot.data.SoundspotArtistParams
import com.kuvandikov.soundspot.data.SoundspotArtistParams.Companion.toQueryMap
import com.kuvandikov.soundspot.data.api.SoundspotEndpoints
import com.kuvandikov.soundspot.domain.models.ApiResponse
import com.kuvandikov.soundspot.domain.models.checkForErrors

class SoundspotArtistDataSource @Inject constructor(
    private val endpoints: SoundspotEndpoints,
    private val dispatchers: CoroutineDispatchers
) {
    suspend operator fun invoke(params: SoundspotArtistParams): Result<ApiResponse> {
        return resultApiCall(dispatchers.network) {
            endpoints.artist(params.id, params.toQueryMap())
                .checkForErrors()
        }
    }
}
