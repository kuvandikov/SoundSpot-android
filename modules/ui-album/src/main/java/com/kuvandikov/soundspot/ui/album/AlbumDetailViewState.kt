/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.album

import android.content.Context
import javax.annotation.concurrent.Immutable
import com.kuvandikov.soundspot.domain.entities.Album
import com.kuvandikov.soundspot.domain.entities.Audios
import com.kuvandikov.soundspot.ui.detail.MediaDetailViewState
import com.kuvandikov.domain.models.Async
import com.kuvandikov.domain.models.Success
import com.kuvandikov.domain.models.Uninitialized

@Immutable
internal data class AlbumDetailViewState(
    val album: Album? = null,
    val albumDetails: Async<Audios> = Uninitialized
) : MediaDetailViewState<Audios> {

    override val isLoaded = album != null
    override val isEmpty = albumDetails is Success && albumDetails.invoke().isEmpty()
    override val title = album?.title

    override fun artwork(context: Context) = album?.photo?.mediumUrl
    override fun details() = albumDetails

    companion object {
        val Empty = AlbumDetailViewState()
    }
}
