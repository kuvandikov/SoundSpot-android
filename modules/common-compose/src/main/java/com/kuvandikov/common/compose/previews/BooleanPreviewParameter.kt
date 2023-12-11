/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.common.compose.previews

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class BooleanPreviewParameter : PreviewParameterProvider<Boolean> {

    override val values: Sequence<Boolean>
        get() = sequenceOf(false, true)
}
