/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import com.kuvandikov.base.imageloading.CoilAppInitializer
import com.kuvandikov.base.inititializer.AppInitializers
import com.kuvandikov.base.inititializer.ThreeTenAbpInitializer
import com.kuvandikov.base.inititializer.TimberInitializer
import com.kuvandikov.base.migrator.AppMigrator
import com.kuvandikov.base.util.CoroutineDispatchers
import com.kuvandikov.data.PreferencesStore
import com.kuvandikov.soundspot.data.migrators.AudiosFtsAppMigration
import com.kuvandikov.soundspot.fcm.FcmTokenRegistrator
import com.kuvandikov.soundspot.notifications.NotificationsInitializer
import com.kuvandikov.soundspot.util.RemoteConfigInitializer

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Singleton
    @Provides
    fun coroutineDispatchers() = CoroutineDispatchers(
        network = Dispatchers.IO,
        io = Dispatchers.IO,
        computation = Dispatchers.Default,
        main = Dispatchers.Main
    )

    @Provides
    fun appInitializers(
        notifications: NotificationsInitializer,
        timberManager: TimberInitializer,
        threeTen: ThreeTenAbpInitializer,
        coilAppInitializer: CoilAppInitializer,
        fcmTokenRegistrator: FcmTokenRegistrator,
        remoteConfigInitializer: RemoteConfigInitializer
    ): AppInitializers {
        return AppInitializers(
            notifications,
            timberManager,
            threeTen,
            coilAppInitializer,
            fcmTokenRegistrator,
            remoteConfigInitializer
        )
    }

    @Provides
    @Singleton
    fun appMigrator(
        dispatchers: CoroutineDispatchers,
        preferencesStore: PreferencesStore,
        audiosFtsAppMigration: AudiosFtsAppMigration
    ) = AppMigrator(dispatchers, preferencesStore, setOf(audiosFtsAppMigration))
}
