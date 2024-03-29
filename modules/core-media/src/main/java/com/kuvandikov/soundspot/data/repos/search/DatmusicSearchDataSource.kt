/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.repos.search

import com.kuvandikov.base.util.CoroutineDispatchers
import com.kuvandikov.data.resultApiCall
import com.kuvandikov.soundspot.data.SoundspotSearchParams
import com.kuvandikov.soundspot.data.SoundspotSearchParams.Companion.toApiRequest
import com.kuvandikov.soundspot.data.api.SoundspotEndpoints
import com.kuvandikov.soundspot.domain.models.ApiResponse
import com.kuvandikov.soundspot.domain.models.checkForErrors
import javax.inject.Inject

class SoundspotSearchDataSource @Inject constructor(
    private val endpoints: SoundspotEndpoints,
    private val dispatchers: CoroutineDispatchers
) {
    suspend operator fun invoke(params: SoundspotSearchParams): Result<ApiResponse> {
        return resultApiCall(dispatchers.network) {
            endpoints.query(params.toApiRequest())
                .checkForErrors()
        }
    }
}
