/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.base.ui.utils.extensions

import androidx.lifecycle.SavedStateHandle

inline fun <reified T : Any> SavedStateHandle.require(key: String): T = requireNotNull(get(key))
