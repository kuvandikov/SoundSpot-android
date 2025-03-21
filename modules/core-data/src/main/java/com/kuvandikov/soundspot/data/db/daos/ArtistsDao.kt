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
import com.kuvandikov.soundspot.domain.entities.Artist

@Dao
abstract class ArtistsDao : PaginatedEntryDao<SoundspotSearchParams, Artist>() {

    @Transaction
    @Query("DELETE FROM artists WHERE name NOT IN (:names)")
    abstract suspend fun deleteExcept(names: Set<String>): Int

    @Transaction
    @Query("SELECT * FROM artists ORDER BY page ASC, search_index ASC")
    abstract override fun entries(): Flow<List<Artist>>

    @Query("SELECT * FROM artists WHERE params = :params ORDER BY page ASC, search_index ASC")
    abstract override fun entries(params: SoundspotSearchParams): Flow<List<Artist>>

    @Transaction
    @Query("SELECT * FROM artists WHERE params = :params and page = :page ORDER BY page ASC, search_index ASC")
    abstract override fun entries(params: SoundspotSearchParams, page: Int): Flow<List<Artist>>

    @Transaction
    @Query("SELECT * FROM artists ORDER BY page ASC, search_index ASC LIMIT :count OFFSET :offset")
    abstract override fun entries(count: Int, offset: Int): Flow<List<Artist>>

    @Transaction
    @Query("SELECT * FROM artists ORDER BY page ASC, search_index ASC")
    abstract override fun entriesPagingSource(): PagingSource<Int, Artist>

    @Transaction
    @Query("SELECT * FROM artists WHERE params = :params ORDER BY page ASC, search_index ASC")
    abstract override fun entriesPagingSource(params: SoundspotSearchParams): PagingSource<Int, Artist>

    @Transaction
    @Query("SELECT * FROM artists WHERE id = :id ORDER BY details_fetched DESC")
    abstract override fun entry(id: String): Flow<Artist>

    @Transaction
    @Query("SELECT * FROM artists WHERE id in (:ids)")
    abstract override fun entriesById(ids: List<String>): Flow<List<Artist>>

    @Transaction
    @Query("SELECT * FROM artists WHERE id = :id")
    abstract override fun entryNullable(id: String): Flow<Artist?>

    @Query("DELETE FROM artists WHERE id = :id")
    abstract override suspend fun delete(id: String): Int

    @Query("DELETE FROM artists WHERE params = :params")
    abstract override suspend fun delete(params: SoundspotSearchParams): Int

    @Query("DELETE FROM artists WHERE params = :params and page = :page")
    abstract override suspend fun delete(params: SoundspotSearchParams, page: Int): Int

    @Query("DELETE FROM artists")
    abstract override suspend fun deleteAll(): Int

    @Query("SELECT COUNT(*) from artists")
    abstract override suspend fun count(): Int

    @Query("SELECT COUNT(*) from artists")
    abstract override fun observeCount(): Flow<Int>

    @Query("SELECT COUNT(*) from artists where params = :params")
    abstract override suspend fun count(params: SoundspotSearchParams): Int

    @Query("SELECT COUNT(*) from artists where id = :id")
    abstract override fun has(id: String): Flow<Int>

    @Query("SELECT COUNT(*) from artists where id = :id")
    abstract override suspend fun exists(id: String): Int
}
