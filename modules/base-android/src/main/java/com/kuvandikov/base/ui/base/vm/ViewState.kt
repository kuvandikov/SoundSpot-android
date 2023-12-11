/*
 * Copyright (C) 2018, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.base.ui.base.vm

interface ViewState

open class TypedViewState<out V>(val value: V) : ViewState
