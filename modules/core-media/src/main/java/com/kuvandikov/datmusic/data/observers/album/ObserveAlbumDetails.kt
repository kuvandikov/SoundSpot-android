/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data.observers.album

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import com.kuvandikov.data.SubjectInteractor
import com.kuvandikov.datmusic.data.DatmusicAlbumParams
import com.kuvandikov.datmusic.data.db.daos.AlbumsDao
import com.kuvandikov.datmusic.data.interactors.album.GetAlbumDetails
import com.kuvandikov.datmusic.domain.entities.Album
import com.kuvandikov.datmusic.domain.entities.Audios

class ObserveAlbum @Inject constructor(
    private val albumsDao: AlbumsDao,
) : SubjectInteractor<DatmusicAlbumParams, Album>() {
    override fun createObservable(params: DatmusicAlbumParams): Flow<Album> = albumsDao.entry(params.id)
}

class ObserveAlbumDetails @Inject constructor(
    private val getAlbumDetails: GetAlbumDetails,
) : SubjectInteractor<GetAlbumDetails.Params, Audios>() {

    override fun createObservable(params: GetAlbumDetails.Params) = getAlbumDetails(params)
}
