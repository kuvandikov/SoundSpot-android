/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data.observers.artist

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import com.kuvandikov.data.SubjectInteractor
import com.kuvandikov.datmusic.data.DatmusicArtistParams
import com.kuvandikov.datmusic.data.db.daos.ArtistsDao
import com.kuvandikov.datmusic.data.interactors.artist.GetArtistDetails
import com.kuvandikov.datmusic.domain.entities.Artist

class ObserveArtist @Inject constructor(
    private val artistsDao: ArtistsDao,
) : SubjectInteractor<DatmusicArtistParams, Artist>() {
    override fun createObservable(params: DatmusicArtistParams): Flow<Artist> = artistsDao.entry(params.id)
}

class ObserveArtistDetails @Inject constructor(
    private val getArtistDetails: GetArtistDetails,
) : SubjectInteractor<GetArtistDetails.Params, Artist>() {
    override fun createObservable(params: GetArtistDetails.Params) = getArtistDetails(params)
}
