/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.kuvandikov.base.di.TestAppModule
import com.kuvandikov.base.testing.TestImageModule
import com.kuvandikov.datmusic.data.db.TestDatabaseModule

@Module(includes = [TestAppModule::class, TestDatabaseModule::class, TestImageModule::class])
@InstallIn(SingletonComponent::class)
class TestModule
