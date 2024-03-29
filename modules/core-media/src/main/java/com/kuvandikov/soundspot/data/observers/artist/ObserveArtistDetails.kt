/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.observers.artist

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import com.kuvandikov.data.SubjectInteractor
import com.kuvandikov.soundspot.data.SoundspotArtistParams
import com.kuvandikov.soundspot.data.db.daos.ArtistsDao
import com.kuvandikov.soundspot.data.interactors.artist.GetArtistDetails
import com.kuvandikov.soundspot.domain.entities.Artist

class ObserveArtist @Inject constructor(
    private val artistsDao: ArtistsDao,
) : SubjectInteractor<SoundspotArtistParams, Artist>() {
    override fun createObservable(params: SoundspotArtistParams): Flow<Artist> = artistsDao.entry(params.id)
}

class ObserveArtistDetails @Inject constructor(
    private val getArtistDetails: GetArtistDetails,
) : SubjectInteractor<GetArtistDetails.Params, Artist>() {
    override fun createObservable(params: GetArtistDetails.Params) = getArtistDetails(params)
}
