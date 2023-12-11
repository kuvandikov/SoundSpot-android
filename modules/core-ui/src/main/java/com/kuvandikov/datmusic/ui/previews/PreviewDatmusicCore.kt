/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.previews

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.jakewharton.threetenabp.AndroidThreeTen
import timber.log.Timber
import com.kuvandikov.base.ui.ThemeState
import com.kuvandikov.common.compose.LocalAnalytics
import com.kuvandikov.common.compose.LocalAppVersion
import com.kuvandikov.common.compose.LocalIsPreviewMode
import com.kuvandikov.common.compose.LocalSnackbarHostState
import com.kuvandikov.datmusic.ui.audios.LocalAudioActionHandler
import com.kuvandikov.datmusic.ui.downloader.LocalAudioDownloadItemActionHandler
import com.kuvandikov.datmusic.ui.downloader.LocalDownloader
import com.kuvandikov.datmusic.ui.playback.LocalPlaybackConnection
import com.kuvandikov.datmusic.ui.snackbar.SnackbarMessagesHost
import com.kuvandikov.navigation.LocalNavigator
import com.kuvandikov.ui.theme.DefaultTheme
import com.kuvandikov.ui.theme.PreviewAppTheme

@Composable
fun PreviewDatmusicCore(
    theme: ThemeState = DefaultTheme,
    changeSystemBar: Boolean = true,
    context: Context = LocalContext.current,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    content: @Composable () -> Unit
) {
    AndroidThreeTen.init(context)
    LaunchedEffect(Unit) {
        Timber.plant(Timber.DebugTree())
    }

    CompositionLocalProvider(
        LocalNavigator provides PreviewNavigator,
        LocalDownloader provides PreviewDownloader,
        LocalPlaybackConnection provides previewPlaybackConnection(),
        LocalAudioActionHandler provides PreviewAudioActionHandler,
        LocalAudioDownloadItemActionHandler provides PreviewAudioDownloadItemActionHandler,
        LocalAnalytics provides PreviewAnalytics,
        LocalSnackbarHostState provides snackbarHostState,
        LocalAppVersion provides "1.0.0",
        LocalIsPreviewMode provides true,
    ) {
        SnackbarMessagesHost(viewModel = PreviewSnackbarMessagesHostViewModel)
        PreviewAppTheme(
            content = content,
            theme = theme,
            changeSystemBar = changeSystemBar,
        )
    }
}
