/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.artists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kuvandikov.base.util.Analytics
import com.kuvandikov.common.compose.LocalAnalytics
import com.kuvandikov.common.compose.previews.CombinedPreview
import com.kuvandikov.soundspot.data.SampleData
import com.kuvandikov.soundspot.domain.entities.Artist
import com.kuvandikov.soundspot.ui.previews.PreviewSoundspotCore
import com.kuvandikov.ui.components.CoverImage
import com.kuvandikov.ui.components.placeholder
import com.kuvandikov.ui.components.shimmer
import com.kuvandikov.ui.theme.AppTheme

object ArtistsDefaults {
    val imageSize = 70.dp
    val nameWidth = 100.dp
}

@Composable
fun ArtistColumn(
    artist: Artist,
    imageSize: Dp = ArtistsDefaults.imageSize,
    nameWidth: Dp = ArtistsDefaults.nameWidth,
    isPlaceholder: Boolean = false,
    analytics: Analytics = LocalAnalytics.current,
    onClick: () -> Unit = {},
) {
    val loadingModifier = Modifier.placeholder(
        visible = isPlaceholder,
        highlight = shimmer(),
    )
    Column(
        verticalArrangement = Arrangement.spacedBy(AppTheme.specs.paddingSmall),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable {
                analytics.click("artist", mapOf("id" to artist.id))
                if (!isPlaceholder) onClick()
            }
            .padding(AppTheme.specs.paddingTiny)
    ) {
        CoverImage(
            data = artist.photo(),
            icon = rememberVectorPainter(Icons.Default.Person),
            shape = CircleShape,
            size = imageSize,
            imageModifier = loadingModifier,
        )

        Text(
            artist.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(nameWidth)
                .then(loadingModifier)
        )
    }
}

@CombinedPreview
@Composable
fun ArtistColumnPreview() = PreviewSoundspotCore {
    Surface {
        ArtistColumn(SampleData.artist())
    }
}
