/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.migration.DisableInstallInCheck
import javax.inject.Singleton
import com.kuvandikov.data.db.DatabaseTxRunner

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun soundspotDatabase(@ApplicationContext context: Context): AppDatabase {
        val builder = Room.databaseBuilder(context, AppDatabase::class.java, "app.db")
//            .addMigrations(MIGRATION_3_4)
//            .fallbackToDestructiveMigration()
        return builder.build()
    }

    @Singleton
    @Provides
    fun databaseTransactionRunner(db: AppDatabase): DatabaseTxRunner = DatabaseTxRunner(db)
}

@Module
@DisableInstallInCheck
object TestDatabaseModule {
    @Singleton
    @Provides
    fun provideTestDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }
}
