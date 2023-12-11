/*
 * Copyright (C) 2021, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.domain.models

interface BaseEntity {
    var params: String

    fun getIdentifier(): String
}

interface PaginatedEntity : BaseEntity {
    var page: Int
}

abstract class BasePaginatedEntity : PaginatedEntity {

    companion object {
        const val defaultParams = ""
        const val defaultPage = 0
    }
}
