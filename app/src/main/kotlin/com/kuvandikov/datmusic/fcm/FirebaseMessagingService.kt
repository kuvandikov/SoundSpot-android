/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.fcm

import android.annotation.SuppressLint
import android.app.Application
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService as FirebaseService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.kuvandikov.base.inititializer.AppInitializer
import com.kuvandikov.base.util.RemoteLogger
import com.kuvandikov.datmusic.data.interactors.RegisterFcmToken
import com.kuvandikov.datmusic.notifications.AppNotification
import com.kuvandikov.datmusic.notifications.notify

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
@AndroidEntryPoint
class FirebaseMessagingService : FirebaseService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        when {
            remoteMessage.data.isNotEmpty() -> notify(AppNotification.fromRemoteMessage(remoteMessage))
            else -> super.onMessageReceived(remoteMessage)
        }
    }
}

class FcmTokenRegistrator @Inject constructor(
    private val registerFcmToken: RegisterFcmToken,
) : AppInitializer, CoroutineScope by MainScope() {
    override fun init(application: Application) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) return@OnCompleteListener
                val token = task.result ?: return@OnCompleteListener

                launch {
                    registerFcmToken(RegisterFcmToken.Params(token))
                        .catch { RemoteLogger.exception(it) }
                        .collect()
                }
            }
        )
    }
}
