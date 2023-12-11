/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.util

import android.content.Context
import java.util.Locale
import okhttp3.Interceptor
import okhttp3.Response
import com.kuvandikov.Config as BaseConfig
import com.kuvandikov.base.util.extensions.androidId
import com.kuvandikov.datmusic.Config

internal class AppHeadersInterceptor(context: Context) : Interceptor {
    private val clientId = context.androidId()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("User-Agent", Config.APP_USER_AGENT)
            .header("Accept-Language", Locale.getDefault().language)
            .run {
                val host = chain.request().url.host
                when (host.contains(BaseConfig.BASE_HOST)) {
                    true -> this.header("X-Datmusic-Id", clientId).header("X-Datmusic-Version", Config.APP_USER_AGENT)
                    else -> this
                }
            }
            .build()
        return chain.proceed(request)
    }
}
