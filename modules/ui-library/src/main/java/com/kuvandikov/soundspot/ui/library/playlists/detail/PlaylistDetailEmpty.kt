/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.library.playlists.detail

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kuvandikov.soundspot.domain.entities.PlaylistItems
import com.kuvandikov.soundspot.ui.detail.MediaDetailEmpty
import com.kuvandikov.soundspot.ui.library.R
import com.kuvandikov.domain.models.Async
import com.kuvandikov.domain.models.Success
import com.kuvandikov.ui.Delayed
import com.kuvandikov.ui.components.EmptyErrorBox

internal class PlaylistDetailEmpty : MediaDetailEmpty<PlaylistItems>() {
    override operator fun invoke(
        list: LazyListScope,
        details: Async<PlaylistItems>,
        isHeaderVisible: Boolean,
        detailsEmpty: Boolean,
        onEmptyRetry: () -> Unit
    ) {
        if (details is Success && detailsEmpty) {
            list.item {
                Delayed {
                    EmptyErrorBox(
                        onRetryClick = onEmptyRetry,
                        message = stringResource(R.string.playlist_empty),
                        retryLabel = stringResource(R.string.playlist_empty_addSongs),
                        modifier = Modifier.fillParentMaxHeight(if (isHeaderVisible) 0.5f else 1f)
                    )
                }
            }
        }
    }
}
