/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.ui.theme

import androidx.compose.runtime.Composable

object AppBarAlphas {
    @Composable
    fun translucentBarAlpha(): Float = when {
        AppTheme.colors.isLightTheme -> 0.97f
        else -> 0.95f
    }
}
