/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.artist

import android.content.Context
import javax.annotation.concurrent.Immutable
import com.kuvandikov.soundspot.domain.entities.Artist
import com.kuvandikov.soundspot.ui.detail.MediaDetailViewState
import com.kuvandikov.domain.models.Async
import com.kuvandikov.domain.models.Success
import com.kuvandikov.domain.models.Uninitialized

@Immutable
internal data class ArtistDetailViewState(
    val artist: Artist? = null,
    val artistDetails: Async<Artist> = Uninitialized
) : MediaDetailViewState<Artist> {

    override val isLoaded = artist != null
    override val isEmpty = artistDetails is Success && artistDetails.invoke().audios.isEmpty()
    override val title = artist?.name

    override fun artwork(context: Context) = artist?.largePhoto()
    override fun details() = artistDetails

    companion object {
        val Empty = ArtistDetailViewState()
    }
}
