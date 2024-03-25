/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.search

import android.os.Parcelable
import androidx.compose.runtime.Stable
import androidx.paging.compose.LazyPagingItems
import com.kuvandikov.soundspot.data.BackendTypes
import com.kuvandikov.soundspot.data.CaptchaSolution
import com.kuvandikov.soundspot.data.SoundspotSearchParams.BackendType
import com.kuvandikov.soundspot.data.SoundspotSearchParams.BackendType.Companion.asBackendTypes
import com.kuvandikov.soundspot.domain.entities.Album
import com.kuvandikov.soundspot.domain.entities.Artist
import com.kuvandikov.soundspot.domain.entities.Audio
import com.kuvandikov.soundspot.domain.models.errors.ApiCaptchaError
import kotlinx.parcelize.Parcelize
import javax.annotation.concurrent.Immutable

@Immutable
internal data class SearchViewState(
    val currentQuery: String = "",
    val filter: SearchFilter = SearchFilter(),
    val captchaError: ApiCaptchaError? = null,
) {

    val hasActiveSearchQuery get() = currentQuery.isNotBlank()

    companion object {
        val Empty = SearchViewState()
    }
}

@Stable
internal data class SearchLazyPagers(
    val audios: LazyPagingItems<Audio>,
    val artists: LazyPagingItems<Artist>,
    val albums: LazyPagingItems<Album>,
) {
    operator fun get(backendType: BackendType): LazyPagingItems<*> = when (backendType) {
        BackendType.AUDIOS -> audios
        BackendType.ARTISTS -> artists
        BackendType.ALBUMS -> albums
    }
}

@Parcelize
internal data class SearchFilter(
    val backends: BackendTypes = DefaultBackends
) : Parcelable {

    val hasAudios get() = backends.contains(BackendType.AUDIOS)
    val hasArtists get() = backends.contains(BackendType.ARTISTS)
    val hasAlbums get() = backends.contains(BackendType.ALBUMS)

    companion object {
        val DefaultBackends = setOf(BackendType.AUDIOS, BackendType.ARTISTS, BackendType.ALBUMS)

        fun from(backends: String?) = SearchFilter(backends?.asBackendTypes() ?: DefaultBackends)
    }
}

@Parcelize
internal data class SearchTrigger(val query: String = "", val captchaSolution: CaptchaSolution? = null) : Parcelable

internal data class SearchEvent(val searchTrigger: SearchTrigger, val searchFilter: SearchFilter)
