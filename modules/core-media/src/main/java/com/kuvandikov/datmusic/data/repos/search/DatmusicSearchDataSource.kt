/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data.repos.search

import javax.inject.Inject
import com.kuvandikov.base.util.CoroutineDispatchers
import com.kuvandikov.data.resultApiCall
import com.kuvandikov.datmusic.data.DatmusicSearchParams
import com.kuvandikov.datmusic.data.DatmusicSearchParams.Companion.toQueryMap
import com.kuvandikov.datmusic.data.api.DatmusicEndpoints
import com.kuvandikov.datmusic.domain.models.ApiResponse
import com.kuvandikov.datmusic.domain.models.checkForErrors

class DatmusicSearchDataSource @Inject constructor(
    private val endpoints: DatmusicEndpoints,
    private val dispatchers: CoroutineDispatchers
) {
    suspend operator fun invoke(params: DatmusicSearchParams): Result<ApiResponse> {
        return resultApiCall(dispatchers.network) {
            endpoints.query()
                .checkForErrors()
        }
    }
}
