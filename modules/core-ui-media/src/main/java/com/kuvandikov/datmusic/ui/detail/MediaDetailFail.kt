/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.detail

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kuvandikov.base.util.localizedMessage
import com.kuvandikov.base.util.localizedTitle
import com.kuvandikov.domain.models.Async
import com.kuvandikov.domain.models.Fail
import com.kuvandikov.ui.components.ErrorBox

open class MediaDetailFail<T> {

    open operator fun invoke(
        list: LazyListScope,
        details: Async<T>,
        onFailRetry: () -> Unit,
    ) {
        if (details is Fail) {
            list.item {
                ErrorBox(
                    title = stringResource(details.error.localizedTitle()),
                    message = stringResource(details.error.localizedMessage()),
                    onRetryClick = onFailRetry,
                    modifier = Modifier.fillParentMaxHeight(0.5f)
                )
            }
        }
    }
}
