/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.library.playlists.detail

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.kuvandikov.base.util.asString
import com.kuvandikov.base.util.localizedTitle
import com.kuvandikov.base.util.toUiMessage
import com.kuvandikov.datmusic.data.observers.playlist.NoResultsForPlaylistFilter
import com.kuvandikov.datmusic.domain.entities.PlaylistItems
import com.kuvandikov.datmusic.ui.detail.MediaDetailFail
import com.kuvandikov.domain.models.Async
import com.kuvandikov.domain.models.Fail
import com.kuvandikov.ui.components.ErrorBox
import com.kuvandikov.ui.theme.AppTheme

internal class PlaylistDetailFail : MediaDetailFail<PlaylistItems>() {

    override operator fun invoke(
        list: LazyListScope,
        details: Async<PlaylistItems>,
        onFailRetry: () -> Unit,
    ) {
        if (details is Fail) {
            list.item {
                val error = details.error
                val errorMessage = details.error.toUiMessage().asString(LocalContext.current)
                when (error) {
                    is NoResultsForPlaylistFilter -> Text(
                        text = errorMessage,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppTheme.specs.padding)
                    )
                    else -> ErrorBox(
                        title = stringResource(details.error.localizedTitle()),
                        message = errorMessage,
                        onRetryClick = onFailRetry,
                        modifier = Modifier.fillParentMaxHeight(0.5f)
                    )
                }
            }
        }
    }
}
