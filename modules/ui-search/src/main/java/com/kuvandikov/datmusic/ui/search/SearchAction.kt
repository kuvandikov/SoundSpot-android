/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.search

import com.kuvandikov.datmusic.data.DatmusicSearchParams
import com.kuvandikov.datmusic.domain.entities.Audio
import com.kuvandikov.datmusic.domain.models.errors.ApiCaptchaError

internal sealed interface SearchAction {
    data class QueryChange(val query: String = "") : SearchAction
    object Search : SearchAction
    data class SelectBackendType(val selected: Boolean, val backendType: DatmusicSearchParams.BackendType) : SearchAction

    data class AddError(val error: Throwable, val onRetry: () -> Unit) : SearchAction
    data class SubmitCaptcha(val captchaError: ApiCaptchaError, val solution: String) : SearchAction

    data class PlayAudio(val audio: Audio) : SearchAction
}
