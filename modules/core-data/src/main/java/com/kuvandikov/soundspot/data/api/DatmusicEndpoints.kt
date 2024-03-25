/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.api

import com.kuvandikov.soundspot.data.SoundspotSearchParams
import com.kuvandikov.soundspot.domain.entities.AlbumId
import com.kuvandikov.soundspot.domain.entities.ArtistId
import com.kuvandikov.soundspot.domain.models.ApiRequest
import com.kuvandikov.soundspot.domain.models.ApiResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface SoundspotEndpoints {

//    https://1AXPVLGBOB-dsn.algolia.net/1/indexes/songs/query
    @JvmSuppressWildcards
    @POST("/1/indexes/songs/query")
    suspend fun query(@Body apiRequest: ApiRequest): ApiResponse

    @JvmSuppressWildcards
    @GET("/multisearch")
    suspend fun multisearch(@QueryMap params: Map<String, Any>, @Query("types[]") vararg types: SoundspotSearchParams.BackendType): ApiResponse

    @JvmSuppressWildcards
    @GET("/search/artists")
    suspend fun searchArtists(@QueryMap params: Map<String, Any>, @Query("types[]") vararg types: SoundspotSearchParams.BackendType): ApiResponse

    @JvmSuppressWildcards
    @GET("/search/albums")
    suspend fun searchAlbums(@QueryMap params: Map<String, Any>, @Query("types[]") vararg types: SoundspotSearchParams.BackendType): ApiResponse

    @JvmSuppressWildcards
    @GET("/artists/{id}")
    suspend fun artist(@Path("id") id: ArtistId, @QueryMap params: Map<String, Any>): ApiResponse

    @JvmSuppressWildcards
    @GET("/albums/{id}")
    suspend fun album(@Path("id") id: AlbumId, @QueryMap params: Map<String, Any>): ApiResponse

    @GET("/bytes/{searchKey}/{audioId}")
    suspend fun bytes(@Path("searchKey") searchKey: String, @Path("audioId") audioId: String): ApiResponse

    @POST("/users/register/fcm")
    @FormUrlEncoded
    suspend fun registerFcmToken(@Field("token") token: String): ApiResponse
}
