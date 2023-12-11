/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.base.testing

import app.cash.turbine.FlowTurbine

/**
 * Waits for first item and then completion.
 * Probably needs a better name
 */
suspend fun <T> FlowTurbine<T>.awaitSingle(): T {
    val item = awaitItem()
    awaitComplete()
    return item
}
