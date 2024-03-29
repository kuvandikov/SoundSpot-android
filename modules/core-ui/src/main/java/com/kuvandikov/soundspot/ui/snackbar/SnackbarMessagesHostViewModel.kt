/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.snackbar

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.kuvandikov.base.ui.SnackbarManager
import com.kuvandikov.base.ui.SnackbarMessage

@HiltViewModel
internal class SnackbarMessagesHostViewModel @Inject constructor(
    private val snackbarManager: SnackbarManager
) : ViewModel() {
    val messages = snackbarManager.messages

    fun onSnackbarActionPerformed(message: SnackbarMessage<*>) = snackbarManager.onMessageActionPerformed(message)
    fun onSnackbarDismissed(message: SnackbarMessage<*>) = snackbarManager.onMessageDismissed(message)
}
