/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.data.db

import androidx.room.RoomDatabase
import javax.inject.Inject
import kotlinx.coroutines.withContext
import com.kuvandikov.base.util.CoroutineDispatchers

/**
 * Tiny class for clearing all tables of database.
 */
class NukeDatabase @Inject constructor(private val dispatchers: CoroutineDispatchers) {
    suspend fun nuke(database: RoomDatabase) = withContext(dispatchers.io) {
        database.clearAllTables()
    }
}
