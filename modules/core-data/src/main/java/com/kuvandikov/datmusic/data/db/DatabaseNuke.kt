/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data.db

import javax.inject.Inject
import com.kuvandikov.data.db.NukeDatabase

class DatabaseNuke @Inject constructor(
    private val nukeDatabase: NukeDatabase,
    private val appDatabase: AppDatabase,
) {
    suspend fun nuke() = nukeDatabase.nuke(appDatabase)
}
