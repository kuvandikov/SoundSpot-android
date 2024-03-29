/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.playback

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.kuvandikov.base.di.TestAppModule
import com.kuvandikov.soundspot.data.db.TestDatabaseModule

@Module(includes = [TestAppModule::class, TestDatabaseModule::class])
@InstallIn(SingletonComponent::class)
class TestModule
