/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.base.util

import android.content.Context
import com.kuvandikov.i18n.UiMessage
import com.kuvandikov.i18n.UiMessage.*
import com.kuvandikov.i18n.UiMessageConvertable

fun UiMessage<*>.asString(context: Context): String = when (this) {
    is Plain -> value
    is Resource -> context.getString(value, *formatArgs.toTypedArray())
    is Error -> context.getString(value.localizedMessage())
}

fun UiMessageConvertable.asString(context: Context) = toUiMessage().asString(context)
