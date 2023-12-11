/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.ui.extensions

import androidx.constraintlayout.compose.ConstrainScope

fun ConstrainScope.centerHorizontally() {
    start.linkTo(parent.start)
    end.linkTo(parent.end)
}
