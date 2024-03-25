/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.settings.premium

import com.qonversion.android.sdk.dto.QPermission
import com.kuvandikov.base.billing.SubscriptionError

internal sealed class PremiumStatus {
    object Unknown : PremiumStatus()
    object NotEnabled : PremiumStatus()

    data class NotSubscribed(val subscriptionError: SubscriptionError) : PremiumStatus()
    data class Subscribed(val premiumPermission: QPermission) : PremiumStatus()

    val isActionable get() = this != Unknown && this != NotEnabled
    val isLoading get() = this == Unknown
}
