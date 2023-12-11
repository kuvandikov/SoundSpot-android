/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data

import com.kuvandikov.datmusic.data.db.TestDatabaseModule
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(includes = [TestDatabaseModule::class])
@InstallIn(SingletonComponent::class)
object TestModule
