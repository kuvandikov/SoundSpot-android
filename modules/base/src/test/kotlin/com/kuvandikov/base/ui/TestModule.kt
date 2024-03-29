/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.base.ui

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.kuvandikov.base.di.TestAppModule

@Module(includes = [TestAppModule::class])
@InstallIn(SingletonComponent::class)
class TestModule
