/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.observers.album

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import com.kuvandikov.data.SubjectInteractor
import com.kuvandikov.soundspot.data.SoundspotAlbumParams
import com.kuvandikov.soundspot.data.db.daos.AlbumsDao
import com.kuvandikov.soundspot.data.interactors.album.GetAlbumDetails
import com.kuvandikov.soundspot.domain.entities.Album
import com.kuvandikov.soundspot.domain.entities.Audios

class ObserveAlbum @Inject constructor(
    private val albumsDao: AlbumsDao,
) : SubjectInteractor<SoundspotAlbumParams, Album>() {
    override fun createObservable(params: SoundspotAlbumParams): Flow<Album> = albumsDao.entry(params.id)
}

class ObserveAlbumDetails @Inject constructor(
    private val getAlbumDetails: GetAlbumDetails,
) : SubjectInteractor<GetAlbumDetails.Params, Audios>() {

    override fun createObservable(params: GetAlbumDetails.Params) = getAlbumDetails(params)
}
