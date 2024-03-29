/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.search

import com.kuvandikov.soundspot.data.SoundspotSearchParams
import com.kuvandikov.soundspot.domain.entities.Audio
import com.kuvandikov.soundspot.domain.models.errors.ApiCaptchaError

internal sealed interface SearchAction {
    data class QueryChange(val query: String = "") : SearchAction
    object Search : SearchAction
    data class SelectBackendType(val selected: Boolean, val backendType: SoundspotSearchParams.BackendType) : SearchAction

    data class AddError(val error: Throwable, val onRetry: () -> Unit) : SearchAction
    data class SubmitCaptcha(val captchaError: ApiCaptchaError, val solution: String) : SearchAction

    data class PlayAudio(val audio: Audio) : SearchAction
}
