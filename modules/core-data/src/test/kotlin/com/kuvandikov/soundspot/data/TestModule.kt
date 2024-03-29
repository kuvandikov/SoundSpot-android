/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data

import com.kuvandikov.soundspot.data.db.TestDatabaseModule
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(includes = [TestDatabaseModule::class])
@InstallIn(SingletonComponent::class)
object TestModule
