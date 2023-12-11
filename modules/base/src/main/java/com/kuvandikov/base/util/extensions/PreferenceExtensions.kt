/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.base.util.extensions

import android.content.SharedPreferences

inline fun SharedPreferences.edit(preferApply: Boolean = false, f: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    editor.f()
    if (preferApply) editor.apply() else editor.commit()
}
