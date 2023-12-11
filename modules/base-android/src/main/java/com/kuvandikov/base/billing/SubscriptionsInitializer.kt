/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.base.billing

import android.app.Application
import com.qonversion.android.sdk.QUserProperties
import com.qonversion.android.sdk.Qonversion
import javax.inject.Inject
import com.kuvandikov.base.inititializer.AppInitializer
import com.kuvandikov.base.util.extensions.androidId

class SubscriptionsInitializer @Inject constructor() : AppInitializer {
    override fun init(application: Application) {
        if (Subscriptions.KEY.isNotBlank()) {
            Qonversion.launch(application, Subscriptions.KEY, false)
            Qonversion.setProperty(QUserProperties.CustomUserId, application.androidId())
        }
    }
}
