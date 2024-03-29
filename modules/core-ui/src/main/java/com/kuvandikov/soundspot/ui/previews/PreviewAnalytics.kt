/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.previews

import timber.log.Timber
import com.kuvandikov.base.util.Analytics
import com.kuvandikov.base.util.LogArgs

internal object PreviewAnalytics : Analytics {
    override fun logEvent(event: String, args: LogArgs) {
        Timber.d("Analytics#logEvent: $event, args: $args")
    }
}
