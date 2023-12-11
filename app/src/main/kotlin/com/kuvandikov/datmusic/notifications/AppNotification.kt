/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.notifications

import com.google.firebase.messaging.RemoteMessage

data class AppNotification(val title: String?, val message: String?, val type: String?, val value: String?) {
    companion object {
        fun fromRemoteMessage(remoteMessage: RemoteMessage): AppNotification {
            val data = remoteMessage.data
            var title = data["title"]
            var message = data["message"]
            val notification = remoteMessage.notification
            if (notification != null) {
                title = notification.title
                message = notification.body
            }
            val type = data["type"]
            val value = data["value"]

            return AppNotification(title, message, type, value)
        }
    }
}
