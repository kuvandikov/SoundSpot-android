/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.previews

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.kuvandikov.base.ui.ColorPalettePreference
import com.kuvandikov.base.ui.DarkModePreference
import com.kuvandikov.base.ui.ThemeState

internal class ThemeStatePreviewParameter : PreviewParameterProvider<ThemeState> {

    override val values: Sequence<ThemeState>
        get() = buildList {
            DarkModePreference.values().forEach { darkMode ->
                ColorPalettePreference.values().forEach { colorPalette ->
                    add(ThemeState(darkMode, colorPalette))
                }
            }
        }.asSequence()
}
