/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.detail

import android.content.Context
import com.kuvandikov.domain.models.Async

interface MediaDetailViewState<DetailType> {
    val isLoading: Boolean get() = details().isLoading
    val isLoaded: Boolean get() = false
    val isEmpty: Boolean get() = false
    val title: String? get() = null

    fun artwork(context: Context): Any? = null
    fun details(): Async<DetailType>
}
