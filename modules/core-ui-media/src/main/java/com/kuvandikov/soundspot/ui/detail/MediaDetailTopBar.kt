/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.kuvandikov.base.util.extensions.muteUntil
import com.kuvandikov.ui.components.AppBarNavigationIcon
import com.kuvandikov.ui.components.AppTopBar

open class MediaDetailTopBar {

    @Composable
    open operator fun invoke(
        title: String,
        collapsedProgress: State<Float>,
        onGoBack: () -> Unit,
    ) {
        AppTopBar(
            title = title,
            collapsedProgress = collapsedProgress.value.muteUntil(0.9f),
            navigationIcon = { AppBarNavigationIcon(onClick = onGoBack) },
        )
    }
}
