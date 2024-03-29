/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.interactors

import androidx.datastore.preferences.core.stringPreferencesKey
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import com.kuvandikov.base.util.CoroutineDispatchers
import com.kuvandikov.data.PreferencesStore
import com.kuvandikov.data.ResultInteractor
import com.kuvandikov.data.resultApiCall
import com.kuvandikov.soundspot.data.api.SoundspotEndpoints
import com.kuvandikov.soundspot.domain.models.checkForErrors

/**
 * Registers FCM tokens.
 * Can be safely called multiple times with the same value because it saves the successfully registered token to not to send it again.
 */
class RegisterFcmToken @Inject constructor(
    private val api: SoundspotEndpoints,
    private val preferences: PreferencesStore,
    private val dispatchers: CoroutineDispatchers
) : ResultInteractor<RegisterFcmToken.Params, String>() {

    companion object {
        private val FCM_REGISTERED_TOKEN = stringPreferencesKey("fcm_registered_token")
    }

    data class Params(val token: String)

    override suspend fun doWork(params: Params): String {
        val lastRegisteredToken = preferences.get(FCM_REGISTERED_TOKEN, "").first()
        return /*if (params.token != lastRegisteredToken)
            resultApiCall(dispatchers.network) { api.registerFcmToken(params.token).checkForErrors() }.map {
                preferences.save(FCM_REGISTERED_TOKEN, params.token)
                it.data.message
            }.getOrThrow()
        else*/ "Token already sent"
    }
}
