/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.base.di

import android.app.Application
import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.kuvandikov.base.util.Analytics
import com.kuvandikov.base.util.FirebaseAppAnalytics

@Module
@InstallIn(SingletonComponent::class)
object BaseModule {

    @Provides
    fun appResources(app: Application): Resources = app.resources

    @Singleton
    @Provides
    fun firebaseAnalytics(@ApplicationContext context: Context): Analytics = FirebaseAppAnalytics(context)
}
