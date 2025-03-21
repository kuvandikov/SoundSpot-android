/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.library.playlists.detail

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kuvandikov.base.util.asString
import com.kuvandikov.base.util.extensions.muteUntil
import com.kuvandikov.soundspot.data.observers.playlist.PlaylistItemSortOption
import com.kuvandikov.soundspot.ui.detail.MediaDetailTopBar
import com.kuvandikov.soundspot.ui.library.R
import com.kuvandikov.ui.LifecycleRespectingBackHandler
import com.kuvandikov.ui.components.*
import com.kuvandikov.ui.components.AppTopBar
import com.kuvandikov.ui.theme.AppTheme

internal class PlaylistDetailTopBar(
    private val filterVisible: Boolean,
    private val setFilterVisible: (Boolean) -> Unit,
    private val searchQuery: String,
    private val onSearchQueryChange: (String) -> Unit = {},
    private val hasSortingOption: Boolean,
    private val sortOptions: List<PlaylistItemSortOption>,
    private val sortOption: PlaylistItemSortOption,
    private val onSortOptionSelect: (PlaylistItemSortOption) -> Unit,
    private val onClearFilter: () -> Unit = {}
) : MediaDetailTopBar() {

    @Composable
    override operator fun invoke(
        title: String,
        collapsedProgress: State<Float>,
        onGoBack: () -> Unit,
    ) {
        val isCollapsedProgress by derivedStateOf {
            if (filterVisible) 1f
            else collapsedProgress.value.muteUntil(0.9f)
        }
        AppTopBar(
            title = title,
            collapsedProgress = isCollapsedProgress,
            navigationIcon = { AppBarNavigationIcon(onClick = onGoBack) },
            filterVisible = filterVisible,
            filterContent = {
                PlaylistDetailFilters(
                    searchQuery = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    onClose = {
                        setFilterVisible(false)
                        onSearchQueryChange("")
                    },
                    hasSortingOption = hasSortingOption,
                    sortOptions = sortOptions,
                    sortOption = sortOption,
                    onSortOptionSelect = onSortOptionSelect,
                )
            },
            actions = {
                IconButton(
                    onClick = { setFilterVisible(true) },
                    onLongClick = onClearFilter,
                    onLongClickLabel = stringResource(R.string.playlist_detail_filter_clear),
                ) {
                    Icon(
                        Icons.Default.FilterList,
                        contentDescription = null,
                        modifier = Modifier.size(AppTheme.specs.iconSizeSmall),
                    )
                }
            }
        )
    }

    @Composable
    fun PlaylistDetailFilters(
        searchQuery: String,
        onQueryChange: (String) -> Unit,
        onClose: () -> Unit,
        hasSortingOption: Boolean,
        sortOptions: List<PlaylistItemSortOption>,
        sortOption: PlaylistItemSortOption,
        onSortOptionSelect: (PlaylistItemSortOption) -> Unit,
        modifier: Modifier = Modifier,
        context: Context = LocalContext.current,
    ) {
        LifecycleRespectingBackHandler(onBack = onClose)
        Row(
            modifier.padding(
                end = AppTheme.specs.padding,
                bottom = AppTheme.specs.paddingTiny
            ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AppBarNavigationIcon(onClick = onClose, Modifier.weight(1f))
            SearchTextField(
                modifier = Modifier.weight(6f),
                value = searchQuery,
                onValueChange = onQueryChange,
                hint = stringResource(R.string.playlist_detail_filter_search_hint),
                autoFocus = true,
            )
            Spacer(Modifier.width(AppTheme.specs.paddingSmall))
            SelectableDropdownMenu(
                modifier = Modifier.weight(1f),
                items = sortOptions,
                selectedItem = sortOption,
                onItemSelect = onSortOptionSelect,
                border = ButtonDefaults.outlinedButtonBorder,
                iconOnly = true,
                leadingIcon = Icons.Default.Sort,
                leadingIconColor = if (hasSortingOption) MaterialTheme.colorScheme.secondary else LocalContentColor.current,
                itemLabelMapper = { it.asString(context) },
                itemSuffixMapper = {
                    if (it == sortOption) {
                        Spacer(Modifier.width(AppTheme.specs.paddingLarge))
                        Icon(
                            rememberVectorPainter(
                                if (it.isDescending) Icons.Default.ArrowDownward
                                else Icons.Default.ArrowUpward
                            ),
                            modifier = Modifier.size(14.dp),
                            contentDescription = null,
                        )
                    }
                }
            )
        }
    }
}
