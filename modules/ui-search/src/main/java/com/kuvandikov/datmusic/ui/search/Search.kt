/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.WindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import com.kuvandikov.common.compose.LocalIsPreviewMode
import com.kuvandikov.common.compose.collectEvent
import com.kuvandikov.common.compose.getNavArgument
import com.kuvandikov.common.compose.previews.CombinedPreview
import com.kuvandikov.common.compose.rememberFlowWithLifecycle
import com.kuvandikov.datmusic.data.DatmusicSearchParams.BackendType
import com.kuvandikov.datmusic.data.SampleData
import com.kuvandikov.datmusic.ui.previews.PreviewDatmusicCore
import com.kuvandikov.navigation.screens.QUERY_KEY
import com.kuvandikov.ui.ProvideScaffoldPadding
import com.kuvandikov.ui.components.ChipsRow
import com.kuvandikov.ui.components.SearchTextField
import com.kuvandikov.ui.theme.AppTheme
import com.kuvandikov.ui.theme.topAppBarTitleStyle
import com.kuvandikov.ui.theme.translucentSurface

@Composable
fun SearchRoute(isPreviewMode: Boolean = LocalIsPreviewMode.current) {
    when {
        isPreviewMode -> SearchPreview()
        else -> Search()
    }
}

@Composable
private fun Search(viewModel: SearchViewModel = hiltViewModel()) {
    Search(
        viewModel = viewModel,
        listState = rememberLazyListState(),
        artistsListState = rememberLazyListState(),
        albumsListState = rememberLazyListState(),
        searchLazyPagers = SearchLazyPagers(
            audios = rememberFlowWithLifecycle(viewModel.pagedAudioList).collectAsLazyPagingItems(),
            minerva = rememberFlowWithLifecycle(viewModel.pagedMinervaList).collectAsLazyPagingItems(),
            flacs = rememberFlowWithLifecycle(viewModel.pagedFlacsList).collectAsLazyPagingItems(),
            artists = rememberFlowWithLifecycle(viewModel.pagedArtistsList).collectAsLazyPagingItems(),
            albums = rememberFlowWithLifecycle(viewModel.pagedAlbumsList).collectAsLazyPagingItems(),
        )
    )
}

@Composable
internal fun Search(
    viewModel: SearchViewModel,
    searchLazyPagers: SearchLazyPagers,
    listState: LazyListState,
    artistsListState: LazyListState,
    albumsListState: LazyListState,
) {
    val viewState by rememberFlowWithLifecycle(viewModel.state)
    Search(
        searchEvent = viewModel.searchEvent,
        viewState = viewState,
        onSearchAction = viewModel::onSearchAction,
        searchLazyPagers = searchLazyPagers,
        listState = listState,
        artistsListState = artistsListState,
        albumsListState = albumsListState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Search(
    viewState: SearchViewState,
    searchEvent: Flow<SearchEvent>,
    onSearchAction: (SearchAction) -> Unit,
    searchLazyPagers: SearchLazyPagers,
    listState: LazyListState,
    artistsListState: LazyListState,
    albumsListState: LazyListState,
) {
    val searchBarHideThreshold = 3
    val searchBarHeight = 200.dp
    val searchBarVisibility = remember { Animatable(0f) }

    // hide search bar when scrolling after some scroll
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .debounce(60)
            .distinctUntilChanged()
            .map { if (listState.firstVisibleItemIndex > searchBarHideThreshold) it else false }
            .map { if (it) 1f else 0f }
            .collectLatest { searchBarVisibility.animateTo(it) }
    }

    // scroll up when new search event is fired
    collectEvent(searchEvent) {
        listState.scrollToItem(0)
    }

    Scaffold(
        topBar = {
            SearchAppBar(
                modifier = Modifier
                    .graphicsLayer {
                        alpha = 1 - searchBarVisibility.value
                        translationY = searchBarHeight.value * (-searchBarVisibility.value)
                    },
                state = viewState,
                onQueryChange = { onSearchAction(SearchAction.QueryChange(it)) },
                onSearch = { onSearchAction(SearchAction.Search) },
                onBackendTypeSelect = { onSearchAction(it) }
            )
        },
    ) { padding ->
        ProvideScaffoldPadding(padding) {
            SearchList(
                viewState = viewState,
                listState = listState,
                artistsListState = artistsListState,
                albumsListState = albumsListState,
                onSearchAction = onSearchAction,
                searchLazyPagers = searchLazyPagers,
            )
        }
    }
}

@Composable
@OptIn(ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class)
private fun SearchAppBar(
    state: SearchViewState,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
    titleModifier: Modifier = Modifier,
    onBackendTypeSelect: (SearchAction.SelectBackendType) -> Unit = {},
    focusManager: FocusManager = LocalFocusManager.current,
    windowInfo: WindowInfo = LocalWindowInfo.current,
    isKeyboardVisible: Boolean = WindowInsets.isImeVisible
) {
    val initialQuery = (getNavArgument(QUERY_KEY) ?: "").toString()
    Box(
        modifier = modifier
            .translucentSurface()
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(bottom = AppTheme.specs.paddingTiny)
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        val hasWindowFocus = windowInfo.isWindowFocused

        var focused by remember { mutableStateOf(false) }
        val searchActive = focused && hasWindowFocus && isKeyboardVisible

        val triggerSearch = {
            onSearch()
            keyboardController?.hide()
            focusManager.clearFocus()
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.specs.paddingSmall)
        ) {
            Text(
                text = stringResource(R.string.search_title),
                style = topAppBarTitleStyle(),
                modifier = titleModifier.padding(start = AppTheme.specs.padding, top = AppTheme.specs.padding),
            )
            var query by rememberSaveable { mutableStateOf(initialQuery) }
            SearchTextField(
                value = query,
                onValueChange = { value ->
                    query = value
                    onQueryChange(value)
                },
                onSearch = { triggerSearch() },
                hint = if (!searchActive) stringResource(R.string.search_hint) else stringResource(R.string.search_hint_query),
                analyticsPrefix = "search",
                modifier = Modifier
                    .padding(horizontal = AppTheme.specs.padding)
                    .onFocusChanged {
                        focused = it.isFocused
                    }
            )

            var backends = state.filter.backends
            // this applies until default selections mean everything is chosen
            if (backends == SearchFilter.DefaultBackends)
                backends = emptySet()

            val filterVisible = searchActive || query.isNotBlank() || backends.isNotEmpty()
            SearchFilterPanel(visible = filterVisible, backends) { selectAction ->
                onBackendTypeSelect(selectAction)
                triggerSearch()
            }
        }
    }
}

@Composable
private fun ColumnScope.SearchFilterPanel(
    visible: Boolean,
    selectedItems: Set<BackendType>,
    onBackendTypeSelect: (SearchAction.SelectBackendType) -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandIn(expandFrom = Alignment.TopCenter) + fadeIn(),
        exit = shrinkOut(shrinkTowards = Alignment.BottomCenter) + fadeOut()
    ) {
        ChipsRow(
            items = BackendType.values().toList(),
            selectedItems = selectedItems,
            onItemSelect = { selected, item ->
                onBackendTypeSelect(SearchAction.SelectBackendType(selected, item))
            },
            labelMapper = {
                stringResource(
                    when (it) {
                        BackendType.AUDIOS -> R.string.search_audios
                        BackendType.ARTISTS -> R.string.search_artists
                        BackendType.ALBUMS -> R.string.search_albums
                        BackendType.MINERVA -> R.string.search_minerva
                        BackendType.FLACS -> R.string.search_flacs
                    }
                )
            }
        )
    }
}

@CombinedPreview
@Composable
fun SearchPreview() = PreviewDatmusicCore {
    Search(
        viewState = SearchViewState.Empty,
        searchEvent = emptyFlow(),
        onSearchAction = {},
        searchLazyPagers = SearchLazyPagers(
            artists = flowOf(PagingData.from(SampleData.list { artist() })).collectAsLazyPagingItems(),
            albums = flowOf(PagingData.from(SampleData.list { album() })).collectAsLazyPagingItems(),
            audios = flowOf(PagingData.from(SampleData.list { audio() })).collectAsLazyPagingItems(),
            minerva = flowOf(PagingData.from(SampleData.list { audio() })).collectAsLazyPagingItems(),
            flacs = flowOf(PagingData.from(SampleData.list { audio() })).collectAsLazyPagingItems(),
        ),
        listState = rememberLazyListState(),
        artistsListState = rememberLazyListState(),
        albumsListState = rememberLazyListState(),
    )
}
