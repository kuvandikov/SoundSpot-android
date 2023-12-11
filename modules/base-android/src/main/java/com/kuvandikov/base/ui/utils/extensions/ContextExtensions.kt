/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.base.ui.utils.extensions

import android.content.Context

fun <T> Context.systemService(name: String): T {
    return getSystemService(name) as T
}
