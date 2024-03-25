/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.snackbar

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import com.kuvandikov.base.util.asString
import com.kuvandikov.common.compose.LocalSnackbarHostState
import com.kuvandikov.common.compose.collectEvent

@Composable
fun SnackbarMessagesHost() {
    SnackbarMessagesHost(viewModel = hiltViewModel())
}

@Composable
internal fun SnackbarMessagesHost(
    snackbarHostState: SnackbarHostState = LocalSnackbarHostState.current,
    viewModel: SnackbarMessagesHostViewModel
) {
    val coroutine = rememberCoroutineScope()
    val context = LocalContext.current
    collectEvent(viewModel.messages) {
        coroutine.launch {
            val message = it.message.asString(context)
            val actionLabel = it.action?.label?.asString(context)
            when (snackbarHostState.showSnackbar(message, actionLabel)) {
                SnackbarResult.ActionPerformed -> viewModel.onSnackbarActionPerformed(it)
                SnackbarResult.Dismissed -> viewModel.onSnackbarDismissed(it)
            }
        }
    }
}
