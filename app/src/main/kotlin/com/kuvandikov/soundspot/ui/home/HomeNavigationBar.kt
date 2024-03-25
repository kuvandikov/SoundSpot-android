/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.kuvandikov.common.compose.previews.BooleanPreviewParameter
import com.kuvandikov.common.compose.previews.CombinedPreview
import com.kuvandikov.soundspot.ui.previews.PreviewSoundspotCore
import com.kuvandikov.navigation.screens.RootScreen
import com.kuvandikov.ui.theme.Theme
import com.kuvandikov.ui.theme.translucentSurfaceColor

internal object HomeNavigationBarDefaults {
    val colors
        @Composable
        get() = NavigationBarItemDefaults.colors(
            indicatorColor = Theme.inanimateColorScheme.secondary,
            selectedTextColor = Theme.inanimateColorScheme.secondary,
            selectedIconColor = Theme.inanimateColorScheme.onSecondary,
            unselectedIconColor = Theme.inanimateColorScheme.onSurface,
            unselectedTextColor = Theme.inanimateColorScheme.onSurface,
        )
}

@Composable
internal fun HomeNavigationBar(
    selectedTab: RootScreen,
    onNavigationSelected: (RootScreen) -> Unit,
    modifier: Modifier = Modifier,
    isPlayerActive: Boolean = false,
) {
    val elevation = if (isPlayerActive) 0.dp else 8.dp
    val color = if (isPlayerActive) Color.Transparent else translucentSurfaceColor()
    val backgroundMod = if (isPlayerActive) Modifier.background(homeBottomNavigationGradient()) else Modifier

    NavigationBar(
        tonalElevation = elevation,
        contentColor = contentColorFor(MaterialTheme.colorScheme.surface),
        containerColor = color,
        windowInsets = WindowInsets.navigationBars,
        modifier = modifier.then(backgroundMod),
    ) {
        HomeNavigationItems.forEach { item ->
            NavigationBarItem(
                selected = selectedTab == item.screen,
                onClick = { onNavigationSelected(item.screen) },
                icon = { HomeNavigationItemIcon(item = item, selected = selectedTab == item.screen) },
                label = { Text(text = stringResource(item.labelRes), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                alwaysShowLabel = true,
                colors = HomeNavigationBarDefaults.colors,
            )
        }
    }
}

@Composable
private fun homeBottomNavigationGradient(color: Color = MaterialTheme.colorScheme.surface) = Brush.verticalGradient(
    listOf(
        color.copy(0.8f),
        color.copy(0.9f),
        color.copy(0.97f),
        color,
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@CombinedPreview
@Composable
private fun HomeNavigationBarPreview(
    @PreviewParameter(BooleanPreviewParameter::class) isPlayerActive: Boolean,
) = PreviewSoundspotCore {
    var selectedTab by remember { mutableStateOf<RootScreen>(RootScreen.Search) }
    Scaffold(
        bottomBar = {
            HomeNavigationBar(
                selectedTab = selectedTab,
                onNavigationSelected = { selectedTab = it },
                isPlayerActive = isPlayerActive,
            )
        }
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Theme.colorScheme.inverseSurface)
                .padding(it)
        )
    }
}
