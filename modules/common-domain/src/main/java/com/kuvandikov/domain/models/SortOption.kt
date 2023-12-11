/*
 * Copyright (C) 2021, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.domain.models

import java.io.Serializable
import com.kuvandikov.i18n.UiMessage
import com.kuvandikov.i18n.UiMessageConvertable

abstract class SortOption<T>(
    open val labelRes: Int,
    open val isDescending: Boolean,
    open val comparator: Comparator<in T>? = null
) : UiMessageConvertable, Serializable {
    abstract fun toggleDescending(): SortOption<T>

    override fun toUiMessage() = UiMessage.Resource(labelRes)

    fun isSameOption(other: SortOption<T>) = labelRes == other.labelRes
}

inline fun <T> compareBySerializable(crossinline selector: (T) -> Comparable<*>?): Comparator<T> =
    object : Serializable, Comparator<T> {
        override fun compare(a: T, b: T) = compareValuesBy(a, b, selector)
    }

inline fun <T> compareByDescendingSerializable(crossinline selector: (T) -> Comparable<*>?): Comparator<T> =
    object : Serializable, Comparator<T> {
        override fun compare(a: T, b: T) = compareValuesBy(b, a, selector)
    }
