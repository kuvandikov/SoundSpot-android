/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.domain.models.errors

class EmptyResultException(override val message: String = "Result was empty") : RuntimeException(message)

fun <T> List<T>?.throwOnEmpty() = if (isNullOrEmpty()) throw EmptyResultException() else this

fun <T> Result<List<T>>.requireNonEmpty(condition: () -> Boolean = { true }): List<T> {
    return getOrThrow().apply { if (condition()) throwOnEmpty() }
}
