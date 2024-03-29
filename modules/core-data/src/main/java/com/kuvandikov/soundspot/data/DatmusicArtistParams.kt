/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data

import com.kuvandikov.soundspot.data.CaptchaSolution.Companion.toQueryMap
import com.kuvandikov.soundspot.domain.entities.ArtistId

data class SoundspotArtistParams(
    val id: ArtistId,
    val captchaSolution: CaptchaSolution? = null,
    val page: Int = 0,
) {

    // used in Room queries
    override fun toString() = "id=$id"

    companion object {
        fun SoundspotArtistParams.toQueryMap(): Map<String, Any> = mutableMapOf<String, Any>(
            "page" to page,
        ).also { map ->
            if (captchaSolution != null) {
                map.putAll(captchaSolution.toQueryMap())
            }
        }
    }
}
