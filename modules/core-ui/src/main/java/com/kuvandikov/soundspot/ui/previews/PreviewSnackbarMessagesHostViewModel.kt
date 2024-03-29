/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.previews

import com.kuvandikov.base.ui.SnackbarManager
import com.kuvandikov.soundspot.ui.snackbar.SnackbarMessagesHostViewModel

internal val PreviewSnackbarManager = SnackbarManager()
internal val PreviewSnackbarMessagesHostViewModel = SnackbarMessagesHostViewModel(snackbarManager = PreviewSnackbarManager)
