/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.i18n

import android.content.res.Resources

interface TextCreator<Params> {
    fun Params.localize(resources: Resources): String
}
