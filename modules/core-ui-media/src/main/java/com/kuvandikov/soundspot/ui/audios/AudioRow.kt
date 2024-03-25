/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.audios

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explicit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.saket.swipe.SwipeAction
import com.kuvandikov.base.util.extensions.interpunctize
import com.kuvandikov.base.util.millisToDuration
import com.kuvandikov.common.compose.previews.CombinedPreview
import com.kuvandikov.common.compose.rememberFlowWithLifecycle
import com.kuvandikov.soundspot.data.SampleData
import com.kuvandikov.soundspot.domain.entities.Audio
import com.kuvandikov.soundspot.playback.PlaybackConnection
import com.kuvandikov.soundspot.playback.models.PlaybackQueue.NowPlayingAudio.Companion.isCurrentAudio
import com.kuvandikov.soundspot.ui.library.playlist.addTo.AddToPlaylistMenu
import com.kuvandikov.soundspot.ui.playback.LocalPlaybackConnection
import com.kuvandikov.soundspot.ui.previews.PreviewSoundspotCore
import com.kuvandikov.ui.components.CoverImage
import com.kuvandikov.ui.components.placeholder
import com.kuvandikov.ui.components.shimmer
import com.kuvandikov.ui.material.ContentAlpha
import com.kuvandikov.ui.material.ProvideContentAlpha
import com.kuvandikov.ui.simpleClickable
import com.kuvandikov.ui.theme.AppTheme

object AudiosDefaults {
    val imageSize = 48.dp
    const val maxLines = 3
}

@Composable
fun AudioRow(
    audio: Audio,
    modifier: Modifier = Modifier,
    imageSize: Dp = AudiosDefaults.imageSize,
    isPlaceholder: Boolean = false,
    onClick: ((Audio) -> Unit)? = null,
    onPlayAudio: ((Audio) -> Unit)? = null,
    playOnClick: Boolean = true,
    includeCover: Boolean = true,
    audioIndex: Int? = null,
    observeNowPlayingAudio: Boolean = true,
    extraActionLabels: List<Int> = emptyList(),
    extraEndSwipeActions: List<SwipeAction> = emptyList(),
    hasAddToPlaylistSwipeAction: Boolean = true,
    hasDownloadSwipeAction: Boolean = true,
    onExtraAction: (AudioItemAction.ExtraAction) -> Unit = {},
    isSwipeable: Boolean = true,
) {
    val (addToPlaylistVisible, setAddToPlaylistVisible) = remember { mutableStateOf(false) }
    val content = @Composable { modifier: Modifier ->
        AudioRowWithMenu(
            audio = audio,
            addToPlaylistVisible = addToPlaylistVisible,
            setAddToPlaylistVisible = setAddToPlaylistVisible,
            modifier = modifier,
            imageSize = imageSize,
            isPlaceholder = isPlaceholder,
            onClick = onClick,
            onPlayAudio = onPlayAudio,
            playOnClick = playOnClick,
            includeCover = includeCover,
            audioIndex = audioIndex,
            observeNowPlayingAudio = observeNowPlayingAudio,
            extraActionLabels = extraActionLabels,
            onExtraAction = onExtraAction,
        )
    }
    if (isSwipeable && !isPlaceholder) {
        AudioBoxWithSwipeActions(
            audio = audio,
            content = { content(Modifier) },
            hasDownloadSwipeAction = hasDownloadSwipeAction,
            hasAddToPlaylistSwipeAction = hasAddToPlaylistSwipeAction,
            extraEndActions = extraEndSwipeActions,
            onAddToPlaylist = { setAddToPlaylistVisible(true) },
            modifier = modifier,
        )
    } else content(modifier)
}

@Composable
private fun AudioRowWithMenu(
    audio: Audio,
    addToPlaylistVisible: Boolean,
    setAddToPlaylistVisible: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    imageSize: Dp = AudiosDefaults.imageSize,
    isPlaceholder: Boolean = false,
    onClick: ((Audio) -> Unit)? = null,
    onPlayAudio: ((Audio) -> Unit)? = null,
    playOnClick: Boolean = true,
    includeCover: Boolean = true,
    audioIndex: Int? = null,
    observeNowPlayingAudio: Boolean = true,
    extraActionLabels: List<Int> = emptyList(),
    onExtraAction: (AudioItemAction.ExtraAction) -> Unit = {},
    actionHandler: AudioActionHandler = LocalAudioActionHandler.current
) {
    var menuVisible by remember { mutableStateOf(false) }
    val contentScaleOnMenuVisible = animateFloatAsState((if (menuVisible) 0.97f else 1f))

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable {
                if (!isPlaceholder) {
                    when {
                        playOnClick -> onPlayAudio?.invoke(audio)
                        onClick != null -> onClick(audio)
                        else -> menuVisible = true
                    }
                }
            }
            .fillMaxWidth()
            .padding(AppTheme.specs.inputPaddings)
    ) {
        AudioRowItem(
            audio = audio,
            isPlaceholder = isPlaceholder,
            imageSize = imageSize,
            includeCover = includeCover,
            audioIndex = audioIndex,
            observeNowPlayingAudio = observeNowPlayingAudio,
            onCoverClick = {
                if (onPlayAudio != null) onPlayAudio(audio)
                else actionHandler(AudioItemAction.Play(audio))
            },
            modifier = Modifier
                .weight(19f)
                .graphicsLayer {
                    scaleX *= contentScaleOnMenuVisible.value
                    scaleY *= contentScaleOnMenuVisible.value
                }
        )

        if (!isPlaceholder) {
            AddToPlaylistMenu(audio, addToPlaylistVisible, setAddToPlaylistVisible)
            AudioDropdownMenu(
                expanded = menuVisible,
                onExpandedChange = { menuVisible = it },
                extraActionLabels = extraActionLabels,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f),
                onDropdownSelect = {
                    val action = AudioItemAction.from(it, audio)
                    when {
                        action is AudioItemAction.Play && onPlayAudio != null -> onPlayAudio(audio)
                        action is AudioItemAction.AddToPlaylist -> setAddToPlaylistVisible(true)
                        else -> action.handleExtraActions(actionHandler, onExtraAction)
                    }
                },
            )
        }
    }
}

@Composable
fun AudioRowItem(
    audio: Audio,
    modifier: Modifier = Modifier,
    imageSize: Dp = AudiosDefaults.imageSize,
    maxLines: Int = AudiosDefaults.maxLines,
    onCoverClick: (Audio) -> Unit = {},
    isPlaceholder: Boolean = false,
    includeCover: Boolean = true,
    audioIndex: Int? = null,
    observeNowPlayingAudio: Boolean = true,
    playbackConnection: PlaybackConnection = LocalPlaybackConnection.current,
) {
    val isCurrentAudio = when (observeNowPlayingAudio) {
        true -> {
            val nowPlayingAudio by rememberFlowWithLifecycle(playbackConnection.nowPlayingAudio)
            nowPlayingAudio.isCurrentAudio(audio, audioIndex)
        }
        else -> false
    }

    val titleTextColor = if (isCurrentAudio) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onBackground

    val loadingModifier = Modifier.placeholder(
        visible = isPlaceholder,
        highlight = shimmer(),
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(AppTheme.specs.padding),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        if (includeCover) {
            CoverImage(
                data = audio.coverUrlSmall ?: audio.coverUrl,
                size = imageSize,
                imageModifier = Modifier
                    .simpleClickable { onCoverClick(audio) }
                    .then(loadingModifier),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.specs.paddingTiny)) {
            Text(
                audio.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                color = titleTextColor,
                modifier = loadingModifier
            )
            ProvideContentAlpha(ContentAlpha.medium) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.specs.paddingTiny),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (audio.explicit)
                        Icon(
                            painter = rememberVectorPainter(Icons.Filled.Explicit),
                            contentDescription = null,
                            modifier = Modifier
                                .size(18.dp)
                                .alignByBaseline(),
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = ContentAlpha.medium),
                        )
                    val artistAndDuration = listOf(audio.artist, audio.durationMillis().millisToDuration()).interpunctize()
                    Text(
                        artistAndDuration,
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                        maxLines = maxLines,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .alignByBaseline()
                            .then(loadingModifier)
                    )
                }
            }
        }
    }
}

@CombinedPreview
@Composable
fun AudioRowPreview() = PreviewSoundspotCore {
    Surface {
        AudioRow(SampleData.audio())
    }
}
