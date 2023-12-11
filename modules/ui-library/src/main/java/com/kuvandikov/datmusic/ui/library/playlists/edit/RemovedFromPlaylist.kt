/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.library.playlists.edit

import android.content.Context
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import com.kuvandikov.base.ui.SnackbarAction
import com.kuvandikov.base.ui.SnackbarMessage
import com.kuvandikov.base.util.asString
import com.kuvandikov.datmusic.domain.entities.PlaylistItem
import com.kuvandikov.datmusic.ui.coreLibrary.R
import com.kuvandikov.i18n.UiMessage

internal data class RemovedFromPlaylist(val playlistItem: PlaylistItem, val removedIndex: Int) :
    SnackbarMessage<PlaylistItem>(
        message = UiMessage.Resource(R.string.playlist_edit_removed),
        action = SnackbarAction(
            UiMessage.Resource(R.string.playlist_edit_removed_undo),
            playlistItem
        )
    ) {

    fun asSnackbar(context: Context, onUndo: () -> Unit): SnackbarData {
        val messageString = message.asString(context)
        return object : SnackbarData {
            override fun performAction() {
                onUndo()
            }

            override fun dismiss() {}

            override val visuals = object : SnackbarVisuals {
                override val actionLabel = action?.label?.asString(context)
                override val duration = SnackbarDuration.Indefinite
                override val message = messageString
                override val withDismissAction = false
            }
        }
    }

    companion object {
        const val SNACKBAR_DURATION_MILLIS = 6 * 1000L
    }
}
