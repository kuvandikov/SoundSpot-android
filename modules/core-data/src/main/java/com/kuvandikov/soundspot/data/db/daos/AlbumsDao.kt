/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.db.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import com.kuvandikov.data.db.PaginatedEntryDao
import com.kuvandikov.soundspot.data.SoundspotSearchParams
import com.kuvandikov.soundspot.domain.entities.Album

@Dao
abstract class AlbumsDao : PaginatedEntryDao<SoundspotSearchParams, Album>() {

    @Transaction
    @Query("DELETE FROM albums WHERE title NOT IN (:titles)")
    abstract suspend fun deleteExcept(titles: Set<String>): Int

    @Transaction
    @Query("SELECT * FROM albums ORDER BY page ASC, search_index ASC")
    abstract override fun entries(): Flow<List<Album>>

    @Query("SELECT * FROM albums WHERE params = :params ORDER BY page ASC, search_index ASC")
    abstract override fun entries(params: SoundspotSearchParams): Flow<List<Album>>

    @Transaction
    @Query("SELECT * FROM albums WHERE params = :params and page = :page ORDER BY page ASC, search_index ASC")
    abstract override fun entries(params: SoundspotSearchParams, page: Int): Flow<List<Album>>

    @Transaction
    @Query("SELECT * FROM albums ORDER BY page ASC, search_index ASC LIMIT :count OFFSET :offset")
    abstract override fun entries(count: Int, offset: Int): Flow<List<Album>>

    @Transaction
    @Query("SELECT * FROM albums ORDER BY page ASC, search_index ASC")
    abstract override fun entriesPagingSource(): PagingSource<Int, Album>

    @Transaction
    @Query("SELECT * FROM albums WHERE params = :params ORDER BY page ASC, search_index ASC")
    abstract override fun entriesPagingSource(params: SoundspotSearchParams): PagingSource<Int, Album>

    @Transaction
    @Query("SELECT * FROM albums WHERE id = :id ORDER BY details_fetched DESC")
    abstract override fun entry(id: String): Flow<Album>

    @Transaction
    @Query("SELECT * FROM albums WHERE id in (:ids)")
    abstract override fun entriesById(ids: List<String>): Flow<List<Album>>

    @Transaction
    @Query("SELECT * FROM albums WHERE id = :id")
    abstract override fun entryNullable(id: String): Flow<Album?>

    @Query("DELETE FROM albums WHERE id = :id")
    abstract override suspend fun delete(id: String): Int

    @Query("DELETE FROM albums WHERE params = :params")
    abstract override suspend fun delete(params: SoundspotSearchParams): Int

    @Query("DELETE FROM albums WHERE params = :params and page = :page")
    abstract override suspend fun delete(params: SoundspotSearchParams, page: Int): Int

    @Query("DELETE FROM albums")
    abstract override suspend fun deleteAll(): Int

    @Query("SELECT COUNT(*) from albums")
    abstract override suspend fun count(): Int

    @Query("SELECT COUNT(*) from albums")
    abstract override fun observeCount(): Flow<Int>

    @Query("SELECT COUNT(*) from albums where params = :params")
    abstract override suspend fun count(params: SoundspotSearchParams): Int

    @Query("SELECT COUNT(*) from albums where id = :id")
    abstract override fun has(id: String): Flow<Int>

    @Query("SELECT COUNT(*) from albums where id = :id")
    abstract override suspend fun exists(id: String): Int
}
