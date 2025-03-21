/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.navigation.screens

import androidx.core.net.toUri
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.kuvandikov.Config
import com.kuvandikov.soundspot.domain.entities.PlaylistId

private const val PATH = "update_playlist"

data class EditPlaylistScreen(
    override val route: String = "$PATH/{$PLAYLIST_ID_KEY}",
    override val rootRoute: String = RootScreen.Library.route,
    override val path: String = PATH,
) : LeafScreen(
    route, rootRoute,
    arguments = listOf(
        navArgument(PLAYLIST_ID_KEY) {
            type = NavType.LongType
        }
    ),
    deepLinks = listOf(
        navDeepLink {
            uriPattern = "${Config.BASE_URL}$path/{$PLAYLIST_ID_KEY}"
        }
    )
) {
    companion object {
        fun buildRoute(id: PlaylistId, root: RootScreen = RootScreen.Library) = "${root.route}/$PATH/$id"
        fun buildUri(id: PlaylistId) = "${Config.BASE_URL}$PATH/$id".toUri()
    }
}
