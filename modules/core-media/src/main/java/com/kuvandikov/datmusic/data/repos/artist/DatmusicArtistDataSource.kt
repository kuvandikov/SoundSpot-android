/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data.repos.artist

import javax.inject.Inject
import com.kuvandikov.base.util.CoroutineDispatchers
import com.kuvandikov.data.resultApiCall
import com.kuvandikov.datmusic.data.DatmusicArtistParams
import com.kuvandikov.datmusic.data.DatmusicArtistParams.Companion.toQueryMap
import com.kuvandikov.datmusic.data.api.DatmusicEndpoints
import com.kuvandikov.datmusic.domain.models.ApiResponse
import com.kuvandikov.datmusic.domain.models.checkForErrors

class DatmusicArtistDataSource @Inject constructor(
    private val endpoints: DatmusicEndpoints,
    private val dispatchers: CoroutineDispatchers
) {
    suspend operator fun invoke(params: DatmusicArtistParams): Result<ApiResponse> {
        return resultApiCall(dispatchers.network) {
            endpoints.artist(params.id, params.toQueryMap())
                .checkForErrors()
        }
    }
}
