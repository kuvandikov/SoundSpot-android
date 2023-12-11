/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.data

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import timber.log.Timber
import com.kuvandikov.base.util.RemoteLogger
import com.kuvandikov.domain.models.DEFAULT_JSON_FORMAT
import com.kuvandikov.domain.models.None
import com.kuvandikov.domain.models.Optional
import com.kuvandikov.domain.models.some

const val REMOTE_CONFIG_FETCH_DELAY = 5000L
const val REMOTE_CONFIG_FETCH_INTERVAL_SECONDS = 3600L

private val format = DEFAULT_JSON_FORMAT

@Singleton
class RemoteConfig @Inject constructor() {

    private val remoteConfig by lazy {
        Firebase.remoteConfig.apply {
            val config = remoteConfigSettings {
                minimumFetchIntervalInSeconds = REMOTE_CONFIG_FETCH_INTERVAL_SECONDS
            }
            setConfigSettingsAsync(config)
        }
    }

    init {
        try {
            remoteConfig.fetchAndActivate().addOnCompleteListener {
                Timber.d("Fetch and activate remote config completed")
            }
        } catch (e: Exception) {
            RemoteLogger.exception(e)
        }
    }

    fun get(key: String): String = remoteConfig.getString(key)

    fun optional(key: String): Optional<String> = get(key).let { if (it.isBlank()) None else some(it) }

    fun <T> optional(key: String, serializer: KSerializer<T>): Optional<T> {
        return optional(key).let {
            when (it) {
                is Optional.Some<String> ->
                    try {
                        some(format.decodeFromString(serializer, it.value))
                    } catch (e: SerializationException) {
                        Timber.e(e)
                        RemoteLogger.exception(e)
                        None
                    }
                else -> Optional.None
            }
        }
    }

    fun <T> get(name: String, serializer: KSerializer<T>, defaultValue: T): T {
        return optional(name, serializer).let {
            when (it) {
                is Optional.None -> defaultValue
                else -> it.value()
            }
        }
    }
}
