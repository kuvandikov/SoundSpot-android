/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.common.compose.previews

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@DarkThemePreview
annotation class ThemeOptionPreview

@Preview(
    name = "Dark Theme",
    group = "Dark Theme",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
)
annotation class DarkThemePreview
