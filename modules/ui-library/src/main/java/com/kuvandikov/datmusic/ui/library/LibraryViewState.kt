/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.library

import javax.annotation.concurrent.Immutable
import com.kuvandikov.datmusic.domain.entities.LibraryItems
import com.kuvandikov.domain.models.Async
import com.kuvandikov.domain.models.Uninitialized

@Immutable
internal data class LibraryViewState(
    val items: Async<LibraryItems> = Uninitialized,
) {
    companion object {
        val Empty = LibraryViewState()
    }
}
