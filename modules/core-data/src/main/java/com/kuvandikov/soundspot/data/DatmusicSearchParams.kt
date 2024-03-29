/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data

import com.kuvandikov.soundspot.data.CaptchaSolution.Companion.toQueryMap
import com.kuvandikov.soundspot.domain.models.ApiRequest

typealias BackendTypes = Set<SoundspotSearchParams.BackendType>

data class SoundspotSearchParams(
    val query: String,
    val captchaSolution: CaptchaSolution? = null,
    val types: List<BackendType> = listOf(BackendType.AUDIOS),
    val page: Int = 0,
) {

    // used as a key in Room/Store
    override fun toString() = "query=$query" +
        when {
            else -> ""
        }

    companion object {
        fun SoundspotSearchParams.toQueryMap(): Map<String, Any> = mutableMapOf<String, Any>(
            "query" to query,
            "page" to page,
        ).also { map ->
            if (captchaSolution != null) {
                map.putAll(captchaSolution.toQueryMap())
            }
        }

        fun SoundspotSearchParams.toApiRequest() = ApiRequest(query, page)

        fun SoundspotSearchParams.withTypes(vararg types: BackendType) = copy(types = types.toList())
    }

    enum class BackendType(val type: String) {
        AUDIOS("audios"), ARTISTS("artists"), ALBUMS("albums");

        override fun toString() = type

        companion object {
            private val map = values().associateBy { it.type }

            fun from(value: String) = map[value] ?: AUDIOS

            private const val separator = "||"
            fun BackendTypes.toQueryParam() = joinToString(separator) { it.type }
            fun String.asBackendTypes() = split(separator).map { from(it) }.toSet()
        }
    }
}
