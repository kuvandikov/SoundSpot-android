/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.albums

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Explicit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kuvandikov.base.util.Analytics
import com.kuvandikov.common.compose.LocalAnalytics
import com.kuvandikov.common.compose.previews.CombinedPreview
import com.kuvandikov.soundspot.data.SampleData
import com.kuvandikov.soundspot.domain.entities.Album
import com.kuvandikov.soundspot.ui.previews.PreviewSoundspotCore
import com.kuvandikov.ui.components.CoverImage
import com.kuvandikov.ui.components.placeholder
import com.kuvandikov.ui.components.shimmer
import com.kuvandikov.ui.material.ContentAlpha
import com.kuvandikov.ui.material.ProvideContentAlpha
import com.kuvandikov.ui.theme.AppTheme

object AlbumsDefaults {
    val imageSize = 150.dp
}

@Composable
fun AlbumColumn(
    album: Album,
    modifier: Modifier = Modifier,
    imageSize: Dp = AlbumsDefaults.imageSize,
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
        modifier = modifier
            .clickable {
                analytics.click("album", mapOf("id" to album.id))
                if (!isPlaceholder) onClick()
            }
            .padding(AppTheme.specs.padding)
    ) {
        CoverImage(
            data = album.photo.mediumUrl,
            size = imageSize,
            icon = rememberVectorPainter(Icons.Default.Album),
            imageModifier = loadingModifier,
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.width(imageSize)
        ) {
            Text(album.title, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = loadingModifier, style = MaterialTheme.typography.bodyLarge)
            ProvideContentAlpha(ContentAlpha.medium) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.specs.paddingTiny),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!album.hasYear && album.explicit)
                        ExplicitIcon()
                    Text(
                        album.artists.firstOrNull()?.name ?: "",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = loadingModifier,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (album.hasYear)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.specs.paddingTiny),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (album.explicit)
                            ExplicitIcon()
                        Text(album.year.toString(), modifier = loadingModifier, style = MaterialTheme.typography.bodyMedium)
                    }
            }
        }
    }
}

@Composable
private fun ExplicitIcon() {
    Icon(
        painter = rememberVectorPainter(Icons.Filled.Explicit),
        contentDescription = null,
        modifier = Modifier.size(16.dp),
        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = ContentAlpha.medium),
    )
}

@CombinedPreview
@Composable
fun AlbumColumnPreview() = PreviewSoundspotCore {
    Surface {
        AlbumColumn(SampleData.album())
    }
}
