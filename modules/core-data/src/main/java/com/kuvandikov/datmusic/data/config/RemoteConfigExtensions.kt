/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data.config

import kotlinx.serialization.builtins.ListSerializer
import com.kuvandikov.data.RemoteConfig
import com.kuvandikov.datmusic.domain.entities.SettingsLink

const val REMOTE_CONFIG_SETTINGS_LINKS_KEY = "settings_links"

fun RemoteConfig.getSettingsLinks() = get(REMOTE_CONFIG_SETTINGS_LINKS_KEY, ListSerializer(SettingsLink.serializer()), emptyList())
