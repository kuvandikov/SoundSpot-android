/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.base.util

import kotlinx.coroutines.CoroutineDispatcher

data class CoroutineDispatchers(
    val network: CoroutineDispatcher,
    val io: CoroutineDispatcher,
    val computation: CoroutineDispatcher,
    val main: CoroutineDispatcher,
)
