/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.settings.premium

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import timber.log.Timber
import com.kuvandikov.base.billing.OnPermissionActive
import com.kuvandikov.base.billing.OnPermissionError
import com.kuvandikov.base.billing.Subscriptions
import com.kuvandikov.base.util.IntentUtils
import com.kuvandikov.base.util.asString
import com.kuvandikov.base.util.toast
import com.kuvandikov.common.compose.LocalIsPreviewMode
import com.kuvandikov.common.compose.previews.CombinedPreview
import com.kuvandikov.common.compose.rememberFlowWithLifecycle
import com.kuvandikov.soundspot.ui.settings.R
import com.kuvandikov.soundspot.ui.settings.SettingsLoadingButton

@Composable
fun PremiumButton(
    modifier: Modifier = Modifier,
    isPreviewMode: Boolean = LocalIsPreviewMode.current,
) {
    when {
        isPreviewMode -> PremiumButtonPreview()
        else -> PremiumButton(modifier, hiltViewModel())
    }
}

@Composable
internal fun PremiumButton(
    modifier: Modifier = Modifier,
    viewModel: PremiumSettingsViewModel
) {
    val premiumStatus by rememberFlowWithLifecycle(viewModel.premiumStatus)
    PremiumButton(
        premiumStatus = premiumStatus,
        onFakeRefresh = viewModel::fakeRefresh,
        onRefresh = viewModel::refreshPremiumStatus,
        modifier = modifier,
    )
}

@Composable
private fun PremiumButton(
    premiumStatus: PremiumStatus,
    onFakeRefresh: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    SettingsLoadingButton(
        modifier = modifier,
        enabled = premiumStatus.isActionable,
        isLoading = premiumStatus.isLoading,
        text = premiumStatus.toButtonText()
    ) {
        if (premiumStatus is PremiumStatus.NotSubscribed) onFakeRefresh()
        premiumStatus.handleClick(
            context = context as Activity,
            onPermissionActive = { onRefresh() },
            onPermissionError = { context.toast(it.asString(context)) }
        )
    }
}

@Composable
private fun PremiumStatus.toButtonText() = when (this) {
    is PremiumStatus.NotEnabled -> stringResource(R.string.settings_premium_disabled)
    is PremiumStatus.NotSubscribed -> stringResource(R.string.settings_premium_subscribe)
    is PremiumStatus.Subscribed -> stringResource(R.string.settings_premium_view)
    else -> ""
}

private fun PremiumStatus.handleClick(
    context: Activity,
    onPermissionActive: OnPermissionActive,
    onPermissionError: OnPermissionError
) {
    when (this) {
        is PremiumStatus.NotSubscribed -> {
            Subscriptions.checkPermissions(
                context = context,
                restoreOrPurchaseOnEmpty = true,
                onPermissionActive = onPermissionActive,
                onPermissionError = onPermissionError,
            )
        }
        is PremiumStatus.Subscribed -> {
            val url = Subscriptions.getSubscriptionUrl(premiumPermission)
            IntentUtils.openUrl(context, url)
        }
        else -> Timber.e("Unhandled action: $this")
    }
}

@CombinedPreview
@Composable
private fun PremiumButtonPreview(modifier: Modifier = Modifier) {
    PremiumButton(
        modifier = modifier,
        premiumStatus = PremiumStatus.NotEnabled,
        onFakeRefresh = {},
        onRefresh = {}
    )
}
