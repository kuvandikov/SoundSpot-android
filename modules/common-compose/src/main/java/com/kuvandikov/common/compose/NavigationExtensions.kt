/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.common.compose

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavBackStackEntry

@Composable
fun getNavArgument(key: String): Any? {
    val owner = LocalViewModelStoreOwner.current
    return if (owner is NavBackStackEntry) owner.arguments?.get(key)
    else null
}
