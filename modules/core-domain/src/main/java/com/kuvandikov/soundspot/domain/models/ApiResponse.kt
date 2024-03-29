/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.domain.models

import com.kuvandikov.soundspot.domain.entities.Album
import com.kuvandikov.soundspot.domain.entities.Artist
import com.kuvandikov.soundspot.domain.entities.Audio
import com.kuvandikov.soundspot.domain.models.errors.ApiErrorException
import com.kuvandikov.soundspot.domain.models.errors.mapToApiError
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable



@Serializable
data class ApiRequest(
    @SerialName("query")
    val query:String =  "",

    @SerialName("page")
    val page: Int
)

@Serializable
data class ApiResponse(
    @SerialName("status")
    val status: String = "ok",

    @SerialName("error")
    val error: Error? = null,

    @SerialName("data")
    val data: Data = Data(),

    @SerialName("hits")
    val hits: List<Audio> = arrayListOf(),
) {

    val isSuccessful get() = status == "ok"

    @Serializable
    data class Error(
        @SerialName("id")
        val id: String = "unknown",

        @SerialName("message")
        var message: String? = null,

        @SerialName("code")
        val code: Int = 0,

        @SerialName("captcha_id")
        val captchaId: Long = 0,

        @SerialName("captcha_img")
        val captchaImageUrl: String = "",

        @SerialName("captcha_index")
        val captchaIndex: Int = -1,
    )

    @Serializable
    data class Data(
        @SerialName("message")
        val message: String = "",

        @SerialName("artist")
        val artist: Artist = Artist(),

        @SerialName("album")
        val album: Album = Album(),

        @SerialName("audios")
        val audios: List<Audio> = arrayListOf(),

        @SerialName("artists")
        val artists: List<Artist> = arrayListOf(),

        @SerialName("albums")
        val albums: List<Album> = arrayListOf(),
    )
}

fun ApiResponse.checkForErrors(): ApiResponse = if (isSuccessful) this
else throw ApiErrorException(error ?: ApiResponse.Error("unknown", "Unknown error"))
    .mapToApiError()
