/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.settings.backup

import javax.annotation.concurrent.Immutable

@Immutable
internal data class BackupRestoreViewState(
    val isBackingUp: Boolean = false,
    val isRestoring: Boolean = false
) {
    companion object {
        val Empty = BackupRestoreViewState()
    }
}
