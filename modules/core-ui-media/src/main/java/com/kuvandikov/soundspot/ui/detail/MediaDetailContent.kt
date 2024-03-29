/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.detail

import androidx.compose.foundation.lazy.LazyListScope
import com.kuvandikov.domain.models.Async

abstract class MediaDetailContent<T> {

    abstract operator fun invoke(
        list: LazyListScope,
        details: Async<T>,
        detailsLoading: Boolean
    ): Boolean
}
