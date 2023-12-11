/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.base.billing

import com.qonversion.android.sdk.QonversionError
import com.qonversion.android.sdk.QonversionErrorCode
import com.kuvandikov.baseAndroid.R
import com.kuvandikov.i18n.UiMessage
import com.kuvandikov.i18n.UiMessageConvertable

open class SubscriptionError(val qonversionError: QonversionError) : Throwable(), UiMessageConvertable {
    override fun toUiMessage(): UiMessage<*> = UiMessage.Plain(qonversionError.description)

    override fun toString() = qonversionError.toString()
}

object SubscriptionNoPermissionsError : SubscriptionError(QonversionError(QonversionErrorCode.ProductNotOwned)) {
    override fun toUiMessage() = UiMessage.Resource(R.string.subscriptions_required)
}

object SubscriptionsNotEnabledError : Throwable(message = "Subscriptions not enabled")
