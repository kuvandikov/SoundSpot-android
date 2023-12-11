/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data

import com.kuvandikov.datmusic.data.CaptchaSolution.Companion.toQueryMap
import com.kuvandikov.datmusic.domain.entities.ArtistId

data class DatmusicArtistParams(
    val id: ArtistId,
    val captchaSolution: CaptchaSolution? = null,
    val page: Int = 0,
) {

    // used in Room queries
    override fun toString() = "id=$id"

    companion object {
        fun DatmusicArtistParams.toQueryMap(): Map<String, Any> = mutableMapOf<String, Any>(
            "page" to page,
        ).also { map ->
            if (captchaSolution != null) {
                map.putAll(captchaSolution.toQueryMap())
            }
        }
    }
}
