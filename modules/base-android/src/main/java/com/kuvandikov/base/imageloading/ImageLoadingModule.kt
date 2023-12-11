/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.base.imageloading

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import com.kuvandikov.base.inititializer.AppInitializer

@InstallIn(SingletonComponent::class)
@Module
abstract class ImageLoadingModule {
    @Binds
    @IntoSet
    abstract fun provideCoilInitializer(bind: CoilAppInitializer): AppInitializer
}
