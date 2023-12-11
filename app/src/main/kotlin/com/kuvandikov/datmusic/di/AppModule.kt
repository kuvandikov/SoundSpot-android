/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import com.kuvandikov.base.billing.SubscriptionsInitializer
import com.kuvandikov.base.imageloading.CoilAppInitializer
import com.kuvandikov.base.inititializer.AppInitializers
import com.kuvandikov.base.inititializer.ThreeTenAbpInitializer
import com.kuvandikov.base.inititializer.TimberInitializer
import com.kuvandikov.base.migrator.AppMigrator
import com.kuvandikov.base.util.CoroutineDispatchers
import com.kuvandikov.data.PreferencesStore
import com.kuvandikov.datmusic.data.migrators.AudiosFtsAppMigration
import com.kuvandikov.datmusic.fcm.FcmTokenRegistrator
import com.kuvandikov.datmusic.notifications.NotificationsInitializer
import com.kuvandikov.datmusic.util.RemoteConfigInitializer

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
        remoteConfigInitializer: RemoteConfigInitializer,
        subscriptionsInitializer: SubscriptionsInitializer,
    ): AppInitializers {
        return AppInitializers(
            notifications,
            timberManager,
            threeTen,
            coilAppInitializer,
            fcmTokenRegistrator,
            remoteConfigInitializer,
            subscriptionsInitializer,
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
