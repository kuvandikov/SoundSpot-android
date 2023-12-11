/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.previews

import com.kuvandikov.base.ui.SnackbarManager
import com.kuvandikov.datmusic.ui.snackbar.SnackbarMessagesHostViewModel

internal val PreviewSnackbarManager = SnackbarManager()
internal val PreviewSnackbarMessagesHostViewModel = SnackbarMessagesHostViewModel(snackbarManager = PreviewSnackbarManager)
