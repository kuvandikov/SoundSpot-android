/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.library.playlists.create

import javax.annotation.concurrent.Immutable
import com.kuvandikov.i18n.ValidationError

@Immutable
internal data class CreatePlaylistViewState(
    val name: String = "",
    val nameError: ValidationError? = null,
) {
    companion object {
        val Empty = CreatePlaylistViewState()
    }
}
