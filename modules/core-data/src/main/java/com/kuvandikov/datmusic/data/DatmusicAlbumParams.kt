/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data

import com.kuvandikov.datmusic.data.CaptchaSolution.Companion.toQueryMap
import com.kuvandikov.datmusic.domain.entities.AlbumId

data class DatmusicAlbumParams(
    val id: AlbumId,
    val ownerId: Long,
    val accessKey: String,
    val captchaSolution: CaptchaSolution? = null,
) {

    // used in Room queries
    override fun toString() = "id=$id"

    companion object {
        fun DatmusicAlbumParams.toQueryMap(): Map<String, Any> = mutableMapOf<String, Any>(
            "owner_id" to ownerId,
            "access_key" to accessKey,
        ).also { map ->
            if (captchaSolution != null) {
                map.putAll(captchaSolution.toQueryMap())
            }
        }
    }
}
