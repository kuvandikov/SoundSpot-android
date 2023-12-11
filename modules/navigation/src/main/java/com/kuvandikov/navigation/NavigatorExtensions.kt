/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import app.cash.turbine.test
import com.kuvandikov.base.util.extensions.findComponentActivity

@Composable
inline fun <reified VM : ViewModel> activityHiltViewModel(): VM {
    return hiltViewModel(viewModelStoreOwner = LocalContext.current.findComponentActivity())
}

// https://stackoverflow.com/a/64961032/2897341
@Composable
inline fun <reified VM : ViewModel> NavBackStackEntry.scopedViewModel(navController: NavController): VM {
    val parentId = destination.parent!!.id
    val parentBackStackEntry = navController.getBackStackEntry(parentId)
    return hiltViewModel(parentBackStackEntry)
}

suspend fun Navigator.assertNextRouteContains(vararg expectedValues: String?) = queue.test {
    val newRoute = awaitItem().route
    expectedValues.filterNotNull().forEach {
        assert(newRoute.contains(it))
    }
}
