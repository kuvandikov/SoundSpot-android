/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.observers.playlist

import com.kuvandikov.soundspot.domain.entities.PlaylistItem
import com.kuvandikov.soundspot.downloader.R
import com.kuvandikov.domain.models.SortOption
import com.kuvandikov.domain.models.compareByDescendingSerializable
import com.kuvandikov.domain.models.compareBySerializable

abstract class PlaylistItemSortOption(
    override val labelRes: Int,
    override val isDescending: Boolean,
    override val comparator: Comparator<PlaylistItem>?,
) : SortOption<PlaylistItem>(labelRes, isDescending, comparator) {
    abstract override fun toggleDescending(): PlaylistItemSortOption
}

object PlaylistItemSortOptions {

    val ALL = listOf(ByCustom(), ByTitle(), ByArtist(), ByAlbum(), ByDuration())

    data class ByCustom(override val isDescending: Boolean = false) : PlaylistItemSortOption(
        R.string.playlist_detail_filter_sort_byCustom,
        isDescending,
        if (isDescending) compareByDescendingSerializable { it.playlistAudio.position }
        else compareBySerializable { it.playlistAudio.position }
    ) {
        override fun toggleDescending() = copy(isDescending = !isDescending)
    }

    data class ByTitle(override val isDescending: Boolean = false) : PlaylistItemSortOption(
        R.string.playlist_detail_filter_sort_byTitle,
        isDescending,
        if (isDescending) compareByDescendingSerializable { it.audio.title }
        else compareBySerializable { it.audio.title }
    ) {
        override fun toggleDescending() = copy(isDescending = !isDescending)
    }

    data class ByArtist(override val isDescending: Boolean = false) : PlaylistItemSortOption(
        R.string.playlist_detail_filter_sort_byArtist,
        isDescending,
        if (isDescending) compareByDescendingSerializable { it.audio.artist }
        else compareBySerializable { it.audio.artist }
    ) {
        override fun toggleDescending() = copy(isDescending = !isDescending)
    }

    data class ByAlbum(override val isDescending: Boolean = false) : PlaylistItemSortOption(
        R.string.playlist_detail_filter_sort_byAlbum,
        isDescending,
        if (isDescending) compareByDescendingSerializable { it.audio.album }
        else compareBySerializable { it.audio.album }
    ) {
        override fun toggleDescending() = copy(isDescending = !isDescending)
    }

    data class ByDuration(override val isDescending: Boolean = true) : PlaylistItemSortOption(
        R.string.playlist_detail_filter_sort_byDuration,
        isDescending,
        if (isDescending) compareByDescendingSerializable { it.audio.duration }
        else compareBySerializable { it.audio.duration }
    ) {
        override fun toggleDescending() = copy(isDescending = !isDescending)
    }
}
