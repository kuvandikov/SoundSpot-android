/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.dp
import com.kuvandikov.common.compose.copy
import com.kuvandikov.common.compose.plus

/**
 * Sum of all previous Scaffold [PaddingValues] in the hierarchy.
 */
internal val LocalScaffoldPadding = compositionLocalOf { PaddingValues(0.dp) }

@Composable
fun ProvideScaffoldPadding(padding: PaddingValues, content: @Composable () -> Unit) {
    val absolutePadding = (LocalScaffoldPadding.current + padding)
        .copy(top = padding.calculateTopPadding()) // reset top padding to avoid double padding because for some goddamn reason M3 Scaffold only adds up top padding
    CompositionLocalProvider(
        LocalScaffoldPadding provides absolutePadding,
        content = content
    )
}

@Composable
fun scaffoldPadding(): PaddingValues = LocalScaffoldPadding.current
