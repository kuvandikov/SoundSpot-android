/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.common.compose

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.staticCompositionLocalOf
import com.kuvandikov.base.util.Analytics

val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("No LocalSnackbarHostState provided")
}

val LocalAnalytics = staticCompositionLocalOf<Analytics> {
    error("No LocalAnalytics provided")
}

val LocalIsPreviewMode = staticCompositionLocalOf<Boolean> {
    error("No LocalIsPreviewMode provided")
}

val LocalAppVersion = staticCompositionLocalOf<String> {
    error("No LocalAppVersion provided")
}
