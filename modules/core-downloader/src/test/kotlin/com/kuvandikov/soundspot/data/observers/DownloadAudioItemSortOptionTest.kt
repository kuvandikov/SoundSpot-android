/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data.observers

import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import com.kuvandikov.base.testing.BaseTest
import com.kuvandikov.base.util.extensions.decodeAsBase64Object
import com.kuvandikov.base.util.extensions.encodeAsBase64String
import com.kuvandikov.soundspot.downloader.observers.DownloadAudioItemSortOption
import com.kuvandikov.soundspot.downloader.observers.DownloadAudioItemSortOptions

@HiltAndroidTest
class DownloadAudioItemSortOptionTest : BaseTest() {

    @Test
    fun `test serialization and deserialization of sort options`() {
        DownloadAudioItemSortOptions.ALL.forEach { sortOption ->
            val sortOptionBase64 = sortOption.encodeAsBase64String()
            assertThat(sortOptionBase64)
                .isNotEmpty()

            val decodedSortOption = sortOptionBase64?.decodeAsBase64Object<DownloadAudioItemSortOption>()
            assertThat(decodedSortOption)
                .isEqualTo(sortOption)
        }
    }

    @Test
    fun `toggleDescending returns copy of reverted descending sort option`() {
        DownloadAudioItemSortOptions.ALL.forEach { sortOption ->
            assertThat(sortOption.toggleDescending().isDescending)
                .isEqualTo(sortOption.isDescending.not())
        }
    }
}
