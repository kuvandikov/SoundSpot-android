/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.domain.entities

typealias LibraryItems = List<LibraryItem>

interface LibraryItem {
    val isUpdatable get() = true
    val isDeletable get() = true
    val isDownloadable get() = false

    fun getLabel(): String
    fun getIdentifier(): String
}
