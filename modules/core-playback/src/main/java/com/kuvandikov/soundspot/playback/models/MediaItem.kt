/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.playback.models

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import com.kuvandikov.soundspot.domain.CoverImageSize
import com.kuvandikov.soundspot.domain.UNKNOWN_ARTIST
import com.kuvandikov.soundspot.domain.UNTITLED_SONG
import com.kuvandikov.soundspot.domain.entities.Audio
import com.kuvandikov.soundspot.domain.entities.mainArtist
import com.kuvandikov.soundspot.playback.album
import com.kuvandikov.soundspot.playback.artist
import com.kuvandikov.soundspot.playback.artworkUri
import com.kuvandikov.soundspot.playback.duration
import com.kuvandikov.soundspot.playback.id
import com.kuvandikov.soundspot.playback.title

fun List<MediaSessionCompat.QueueItem>?.toMediaIdList(): List<MediaId> {
    return this?.map { it.description.mediaId?.toMediaId() ?: MediaId() } ?: emptyList()
}

fun List<String>.toMediaIds(): List<MediaId> {
    return this.map { it.toMediaId() }
}

fun List<String>.toMediaAudioIds(): List<String> {
    return this.map { it.toMediaId().value }
}

fun List<Audio?>.toQueueItems(): List<MediaSessionCompat.QueueItem> {
    return filterNotNull().mapIndexed { index, audio ->
        MediaSessionCompat.QueueItem(audio.toMediaDescription(), (audio.id + index).hashCode().toLong())
    }
}

fun Audio.toMediaDescription(): MediaDescriptionCompat {
    return MediaDescriptionCompat.Builder()
        .setTitle(title)
        .setMediaId(MediaId(MEDIA_TYPE_AUDIO, id).toString())
        .setSubtitle(artist)
        .setDescription(album)
        .setIconUri(coverUri()).build()
}

fun Audio.toMediaItem(): MediaBrowserCompat.MediaItem {
    return MediaBrowserCompat.MediaItem(
        MediaDescriptionCompat.Builder()
            .setMediaId(MediaId(MEDIA_TYPE_AUDIO, id).toString())
            .setTitle(title)
            .setIconUri(coverUri())
            .setSubtitle(artist)
            .build(),
        MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
    )
}

fun List<Audio>?.toMediaItems() = this?.map { it.toMediaItem() } ?: emptyList()

fun Audio.toMediaMetadata(builder: MediaMetadataCompat.Builder): MediaMetadataCompat.Builder = builder.apply {
    putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
    putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
    putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
    putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, MediaId(MEDIA_TYPE_AUDIO, id).toString())
    putLong(MediaMetadataCompat.METADATA_KEY_DURATION, durationMillis())
    putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, coverUri(CoverImageSize.LARGE).toString())
    putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, null)
}

fun MediaMetadataCompat.toAudio() = Audio(
    id = id.toMediaId().value,
    artist = artist ?: UNKNOWN_ARTIST,
    title = title ?: UNTITLED_SONG,
    duration = (duration / 1000).toInt(),
    coverUrl = artworkUri.toString()
)

fun MediaMetadataCompat.toArtistSearchQuery() = "${artist?.mainArtist()}"
fun MediaMetadataCompat.toAlbumSearchQuery() = "${artist?.mainArtist()} $album"
