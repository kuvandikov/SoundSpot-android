/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.data.db

import androidx.room.RoomDatabase

open class DatabaseTxRunner(private val db: RoomDatabase) {
    suspend fun invoke(run: () -> Unit) = db.runInTransaction(run)
}
