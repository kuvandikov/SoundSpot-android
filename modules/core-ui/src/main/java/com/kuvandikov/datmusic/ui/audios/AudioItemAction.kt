/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.audios

import com.kuvandikov.datmusic.domain.entities.Audio
import com.kuvandikov.datmusic.ui.R

sealed class AudioItemAction(open val audio: Audio) {
    data class Play(override val audio: Audio) : AudioItemAction(audio)
    data class PlayNext(override val audio: Audio) : AudioItemAction(audio)
    data class Download(override val audio: Audio) : AudioItemAction(audio)
    data class DownloadById(override val audio: Audio) : AudioItemAction(audio)
    data class AddToPlaylist(override val audio: Audio) : AudioItemAction(audio)
    data class ExtraAction(val actionLabelRes: Int, override val audio: Audio) : AudioItemAction(audio)

    fun handleExtraAction(extraActionLabelRes: Int, actionHandler: AudioActionHandler, onExtraAction: (ExtraAction) -> Unit) =
        handleExtraActions(actionHandler) {
            when (it.actionLabelRes) {
                extraActionLabelRes -> onExtraAction(it)
                else -> actionHandler(it)
            }
        }

    fun handleExtraActions(actionHandler: AudioActionHandler, onExtraAction: (ExtraAction) -> Unit) = when (this is ExtraAction) {
        true -> onExtraAction(this)
        else -> actionHandler(this)
    }

    companion object {
        fun from(actionLabelRes: Int, audio: Audio) = when (actionLabelRes) {
            R.string.audio_menu_play -> Play(audio)
            R.string.audio_menu_playNext -> PlayNext(audio)
            R.string.audio_menu_download -> Download(audio)
            R.string.audio_menu_downloadById -> DownloadById(audio)
            R.string.playlist_addTo -> AddToPlaylist(audio)
            else -> ExtraAction(actionLabelRes, audio)
        }
    }
}
