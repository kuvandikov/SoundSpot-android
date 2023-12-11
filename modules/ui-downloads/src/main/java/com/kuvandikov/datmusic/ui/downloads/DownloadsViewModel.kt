/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.downloads

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import com.kuvandikov.base.util.Analytics
import com.kuvandikov.base.util.extensions.getMutableStateFlow
import com.kuvandikov.base.util.extensions.simpleName
import com.kuvandikov.base.util.extensions.stateInDefault
import com.kuvandikov.base.util.searchQueryAnalytics
import com.kuvandikov.data.PreferencesStore
import com.kuvandikov.datmusic.domain.entities.AudioDownloadItem
import com.kuvandikov.datmusic.downloader.Downloader
import com.kuvandikov.datmusic.downloader.observers.DownloadAudioItemSortOption
import com.kuvandikov.datmusic.downloader.observers.DownloadStatusFilter
import com.kuvandikov.datmusic.downloader.observers.ObserveDownloads
import com.kuvandikov.datmusic.downloader.observers.failWithNoResultsIfEmpty
import com.kuvandikov.datmusic.playback.PlaybackConnection
import com.kuvandikov.domain.models.delayLoading
import com.kuvandikov.domain.models.filterSuccess

@HiltViewModel
internal class DownloadsViewModel @Inject constructor(
    handle: SavedStateHandle,
    preferencesStore: PreferencesStore,
    private val observeDownloads: ObserveDownloads,
    private val playbackConnection: PlaybackConnection,
    private val analytics: Analytics,
    private val downloader: Downloader,
) : ViewModel() {

    private val defaultParams = ObserveDownloads.Params()
    private val downloadsParamsState = MutableStateFlow(defaultParams)
    private val searchQueryState = handle.getMutableStateFlow("search_query", viewModelScope, defaultParams.query)
    private val audiosSortOptionState = preferencesStore.getStateFlow("sort_option", viewModelScope, defaultParams.audiosSortOption)
    private val statusFiltersState = preferencesStore.getStateFlow("status_filters", viewModelScope, defaultParams.statusFilters)

    private val downloads = observeDownloads.asyncFlow

    private val newDownloadPositionEventChannel = Channel<Int>(Channel.CONFLATED)
    val newDownloadPositionEvent = newDownloadPositionEventChannel.receiveAsFlow()

    val state = combine(downloads.delayLoading(), downloadsParamsState) { downloads, params ->
        DownloadsViewState(downloads.failWithNoResultsIfEmpty(params), params)
    }.stateInDefault(viewModelScope, DownloadsViewState.Empty)

    init {
        buildDownloadsParamsState()
        buildNewDownloadPositionEvent()
        viewModelScope.launch {
            downloadsParamsState
                .debounce(60)
                .collect(observeDownloads::invoke)
        }
        viewModelScope.launch {
            searchQueryState.searchQueryAnalytics(analytics, "downloads.filter")
        }
    }

    private fun buildDownloadsParamsState() = viewModelScope.launch {
        launch {
            searchQueryState.collect {
                downloadsParamsState.value = downloadsParamsState.value.copy(query = it)
            }
        }
        launch {
            statusFiltersState.collect {
                downloadsParamsState.value = downloadsParamsState.value.copy(statusFilters = it)
            }
        }
        launch {
            audiosSortOptionState.collect { sortOption ->
                val current = downloadsParamsState.value
                downloadsParamsState.value = current.copy(
                    audiosSortOption = sortOption,
                    audiosSortOptions = current.audiosSortOptions.map { if (it.isSameOption(sortOption)) sortOption else it }
                )
            }
        }
    }

    private fun buildNewDownloadPositionEvent() = viewModelScope.launch {
        launch {
            downloader.newDownloadId.collectLatest { dlId ->
                val downloads = observeDownloads.execute(downloadsParamsState.value)
                val newDownloadPosition = downloads.audios.indexOfFirst { it.downloadRequest.id == dlId }
                if (newDownloadPosition != -1) {
                    newDownloadPositionEventChannel.send(newDownloadPosition)
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQueryState.value = query
    }

    fun onAudiosSortOptionSelect(sortOption: DownloadAudioItemSortOption) {
        analytics.event("downloads.filter.sort", mapOf("type" to sortOption.simpleName, "descending" to sortOption.isDescending))
        val isReselecting = sortOption.isSameOption(audiosSortOptionState.value)
        audiosSortOptionState.value = if (isReselecting) sortOption.toggleDescending() else sortOption
    }

    fun onStatusFilterSelect(statusFilter: DownloadStatusFilter) {
        analytics.event("downloads.filter.status", mapOf("status" to statusFilter.name))
        val current = statusFiltersState.value
        // allow multiple selections except when default is selected
        statusFiltersState.value = when {
            statusFilter.isDefault -> defaultParams.defaultStatusFilters // reset to default
            current.contains(statusFilter) -> current - statusFilter // deselect
            else -> statusFiltersState.value + statusFilter // select
        }.let {
            when {
                it.isEmpty() -> defaultParams.defaultStatusFilters // reset to default
                !statusFilter.isDefault -> it.filterNot { it.isDefault }.toSet() // remove default
                else -> it // has no default but has some selections
            }
        }.toHashSet()
    }

    fun onClearFilter() {
        analytics.event("downloads.filter.clear")
        searchQueryState.value = ""
        audiosSortOptionState.value = defaultParams.audiosSortOption
        statusFiltersState.value = defaultParams.defaultStatusFilters
    }

    fun playAudioDownload(audioDownloadItem: AudioDownloadItem) = viewModelScope.launch {
        val downloadAudios = downloads.filterSuccess().first()
        val audioIds = downloadAudios.audios.map { it.audio.id }
        val downloadIndex = audioIds.indexOf(audioDownloadItem.audio.id)
        if (downloadIndex < 0) {
            Timber.e("Audio not found in downloads: ${audioDownloadItem.audio.id}")
            return@launch
        }
        if (downloadsParamsState.value.hasNoFilters) {
            playbackConnection.playFromDownloads(downloadIndex)
        } else playbackConnection.playFromDownloads(downloadIndex, audioIds)
    }
}
