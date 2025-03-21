/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.db.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import com.kuvandikov.soundspot.domain.entities.Audio
import com.kuvandikov.soundspot.domain.entities.DownloadRequest
import com.kuvandikov.soundspot.domain.entities.PlaylistId
import com.kuvandikov.soundspot.domain.entities.PlaylistItem

@Dao
abstract class AudiosFtsDao {

    @Transaction
    @Query(
        """
        SELECT a.* FROM audios as a
        INNER JOIN audios_fts AS fts ON a.id = fts.id
        WHERE audios_fts MATCH :query
        GROUP BY a.id
        """
    )
    abstract fun search(query: String): Flow<List<Audio>>

    @Transaction
    @Query(
        """
        SELECT pa.* FROM playlist_audios as pa
        INNER JOIN audios_fts AS fts ON pa.audio_id = fts.id
        WHERE pa.playlist_id = :playlistId
            AND audios_fts MATCH :query
        GROUP BY pa.id
        ORDER BY pa.position
        """
    )
    abstract fun searchPlaylist(playlistId: PlaylistId, query: String): Flow<List<PlaylistItem>>

    @Transaction
    @Query(
        """
        SELECT d.* FROM download_requests as d
        INNER JOIN audios_fts AS fts ON d.id = fts.id
        WHERE d.entity_type = 'Audio' 
            AND audios_fts MATCH :query
        GROUP BY d.id
        ORDER BY d.created_at DESC, d.id ASC
        """
    )
    abstract fun searchDownloads(query: String): Flow<List<DownloadRequest>>
}
