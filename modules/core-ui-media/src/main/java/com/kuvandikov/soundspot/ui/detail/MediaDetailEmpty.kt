/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.detail

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import com.kuvandikov.domain.models.Async
import com.kuvandikov.domain.models.Success
import com.kuvandikov.ui.components.EmptyErrorBox

open class MediaDetailEmpty<T> {

    open operator fun invoke(
        list: LazyListScope,
        details: Async<T>,
        isHeaderVisible: Boolean,
        detailsEmpty: Boolean,
        onEmptyRetry: () -> Unit
    ) {
        if (details is Success && detailsEmpty) {
            list.item {
                EmptyErrorBox(
                    onRetryClick = onEmptyRetry,
                    modifier = Modifier.fillParentMaxHeight(if (isHeaderVisible) 0.5f else 1f)
                )
            }
        }
    }
}
