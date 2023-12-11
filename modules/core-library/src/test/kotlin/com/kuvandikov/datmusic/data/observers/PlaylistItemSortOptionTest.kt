/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data.observers

import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import com.kuvandikov.base.testing.BaseTest
import com.kuvandikov.base.util.extensions.decodeAsBase64Object
import com.kuvandikov.base.util.extensions.encodeAsBase64String
import com.kuvandikov.datmusic.data.observers.playlist.PlaylistItemSortOption
import com.kuvandikov.datmusic.data.observers.playlist.PlaylistItemSortOptions

@HiltAndroidTest
class PlaylistItemSortOptionTest : BaseTest() {

    @Test
    fun `test serialization and deserialization of sort options`() {
        PlaylistItemSortOptions.ALL.forEach { sortOption ->
            val sortOptionBase64 = sortOption.encodeAsBase64String()
            assertThat(sortOptionBase64)
                .isNotEmpty()

            val decodedSortOption = sortOptionBase64?.decodeAsBase64Object<PlaylistItemSortOption>()
            assertThat(decodedSortOption)
                .isEqualTo(sortOption)
        }
    }

    @Test
    fun `toggleDescending returns copy of reverted descending sort option`() {
        PlaylistItemSortOptions.ALL.forEach { sortOption ->
            assertThat(sortOption.toggleDescending().isDescending)
                .isEqualTo(sortOption.isDescending.not())
        }
    }
}
