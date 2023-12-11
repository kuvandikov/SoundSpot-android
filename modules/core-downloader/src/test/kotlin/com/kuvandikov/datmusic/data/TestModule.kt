/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.kuvandikov.base.di.TestAppModule
import com.kuvandikov.datmusic.data.db.TestDatabaseModule

@Module(
    includes = [
        TestAppModule::class,
        TestDatabaseModule::class,
        TestDownloaderModule::class,
    ]
)
@InstallIn(SingletonComponent::class)
class TestModule
