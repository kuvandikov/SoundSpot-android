/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.domain

enum class CoverImageSize(val type: String, val maxSize: Int) {
    LARGE("large", 1200), MEDIUM("medium", 600), SMALL("small", 300)
}
