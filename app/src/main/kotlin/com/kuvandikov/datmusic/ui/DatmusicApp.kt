/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.plusAssign
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.kuvandikov.base.util.Analytics
import com.kuvandikov.common.compose.LocalAnalytics
import com.kuvandikov.common.compose.LocalAppVersion
import com.kuvandikov.common.compose.LocalIsPreviewMode
import com.kuvandikov.common.compose.LocalSnackbarHostState
import com.kuvandikov.common.compose.previews.CombinedPreview
import com.kuvandikov.common.compose.previews.FontScalePreview
import com.kuvandikov.common.compose.previews.LocalePreview
import com.kuvandikov.common.compose.rememberFlowWithLifecycle
import com.kuvandikov.datmusic.BuildConfig
import com.kuvandikov.datmusic.ui.audios.AudioActionHandler
import com.kuvandikov.datmusic.ui.audios.LocalAudioActionHandler
import com.kuvandikov.datmusic.ui.audios.audioActionHandler
import com.kuvandikov.datmusic.ui.downloader.AudioDownloadItemActionHandler
import com.kuvandikov.datmusic.ui.downloader.DownloaderHost
import com.kuvandikov.datmusic.ui.downloader.LocalAudioDownloadItemActionHandler
import com.kuvandikov.datmusic.ui.downloads.audioDownloadItemActionHandler
import com.kuvandikov.datmusic.ui.home.Home
import com.kuvandikov.datmusic.ui.playback.PlaybackHost
import com.kuvandikov.datmusic.ui.playback.PlaybackViewModel
import com.kuvandikov.datmusic.ui.previews.PreviewDatmusicCore
import com.kuvandikov.datmusic.ui.snackbar.SnackbarMessagesHost
import com.kuvandikov.navigation.NavigatorHost
import com.kuvandikov.navigation.activityHiltViewModel
import com.kuvandikov.navigation.rememberBottomSheetNavigator
import com.kuvandikov.ui.ThemeViewModel
import com.kuvandikov.ui.theme.AppTheme

@Composable
fun DatmusicApp(
    playbackViewModel: PlaybackViewModel = activityHiltViewModel(),
) = DatmusicCore {
    DatmusicAppContent(
        onPlayingTitleClick = playbackViewModel::onTitleClick,
        onPlayingArtistClick = playbackViewModel::onArtistClick,
    )
}

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
@Composable
private fun DatmusicAppContent(
    onPlayingTitleClick: () -> Unit,
    onPlayingArtistClick: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberAnimatedNavController(),
) {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    navController.navigatorProvider += bottomSheetNavigator
    ModalBottomSheetLayout(bottomSheetNavigator, modifier) {
        Home(
            navController = navController,
            onPlayingTitleClick = onPlayingTitleClick,
            onPlayingArtistClick = onPlayingArtistClick,
        )
    }
}

// Could be renamed to DatmusicCoreViewModel if more things are injected
@HiltViewModel
private class AnalyticsViewModel @Inject constructor(val analytics: Analytics) : ViewModel()

@Composable
private fun DatmusicCore(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    themeViewModel: ThemeViewModel = hiltViewModel(),
    analyticsViewModel: AnalyticsViewModel = hiltViewModel(),
    appVersion: String = BuildConfig.VERSION_NAME,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalSnackbarHostState provides snackbarHostState,
        LocalAnalytics provides analyticsViewModel.analytics,
        LocalAppVersion provides appVersion,
        LocalIsPreviewMode provides false,
    ) {
        SnackbarMessagesHost()
        val themeState by rememberFlowWithLifecycle(themeViewModel.themeState)
        AppTheme(themeState, modifier) {
            NavigatorHost {
                DownloaderHost {
                    PlaybackHost {
                        DatmusicActionHandlers(content = content)
                    }
                }
            }
        }
    }
}

@Composable
private fun DatmusicActionHandlers(
    content: @Composable () -> Unit,
    audioActionHandler: AudioActionHandler = audioActionHandler(),
    audioDownloadItemActionHandler: AudioDownloadItemActionHandler = audioDownloadItemActionHandler(),
) {
    CompositionLocalProvider(
        LocalAudioActionHandler provides audioActionHandler,
        LocalAudioDownloadItemActionHandler provides audioDownloadItemActionHandler,
    ) {
        content()
    }
}

@CombinedPreview
@LocalePreview
@FontScalePreview
@Composable
fun DatmusicAppPreview() = PreviewDatmusicCore {
    DatmusicAppContent(
        onPlayingTitleClick = {},
        onPlayingArtistClick = {}
    )
}
