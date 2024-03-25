/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.kuvandikov.base.ui.SnackbarManager
import com.kuvandikov.base.util.Analytics
import com.kuvandikov.base.util.extensions.getMutableStateFlow
import com.kuvandikov.base.util.extensions.stateInDefault
import com.kuvandikov.soundspot.data.CaptchaSolution
import com.kuvandikov.soundspot.data.SoundspotSearchParams
import com.kuvandikov.soundspot.data.SoundspotSearchParams.BackendType
import com.kuvandikov.soundspot.data.SoundspotSearchParams.Companion.withTypes
import com.kuvandikov.soundspot.data.observers.search.ObservePagedSoundspotSearch
import com.kuvandikov.soundspot.domain.entities.Album
import com.kuvandikov.soundspot.domain.entities.Artist
import com.kuvandikov.soundspot.domain.entities.Audio
import com.kuvandikov.soundspot.domain.models.errors.ApiCaptchaError
import com.kuvandikov.soundspot.playback.PlaybackConnection
import com.kuvandikov.navigation.screens.QUERY_KEY
import com.kuvandikov.navigation.screens.SEARCH_BACKENDS_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
internal class SearchViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val audiosPager: ObservePagedSoundspotSearch<Audio>,
    private val artistsPager: ObservePagedSoundspotSearch<Artist>,
    private val albumsPager: ObservePagedSoundspotSearch<Album>,
    private val snackbarManager: SnackbarManager,
    private val analytics: Analytics,
    private val playbackConnection: PlaybackConnection,
) : ViewModel() {

    companion object {
        const val SEARCH_DEBOUNCE_MILLIS = 400L
    }

    private val initialQuery = handle[QUERY_KEY] ?: ""
    private val searchQueryState = handle.getMutableStateFlow(initialQuery, viewModelScope, initialQuery)
    private val searchFilterState = handle.getMutableStateFlow("search_filter", viewModelScope, SearchFilter.from(handle[SEARCH_BACKENDS_KEY]))
    private val searchTriggerState = handle.getMutableStateFlow("search_trigger", viewModelScope, SearchTrigger(initialQuery))

    private val captchaError = MutableStateFlow<ApiCaptchaError?>(null)

    private val pendingActions = MutableSharedFlow<SearchAction>()

    val pagedAudioList get() = audiosPager.flow.cachedIn(viewModelScope)
    val pagedArtistsList get() = artistsPager.flow.cachedIn(viewModelScope)
    val pagedAlbumsList get() = albumsPager.flow.cachedIn(viewModelScope)

    private val onSearchEventChannel = Channel<SearchEvent>(Channel.CONFLATED)
    val searchEvent = onSearchEventChannel.receiveAsFlow()

    val state = combine(
        searchTriggerState.map { it.query }, searchFilterState, captchaError,
        transform = ::SearchViewState
    ).stateInDefault(viewModelScope, SearchViewState.Empty)

    init {
        viewModelScope.launch {
            pendingActions.collectLatest { action ->
                when (action) {
                    is SearchAction.QueryChange -> {
                        searchQueryState.value = action.query
                    }
                    is SearchAction.Search -> searchTriggerState.value = SearchTrigger(searchQueryState.value)
                    is SearchAction.SelectBackendType -> selectBackendType(action)
                    is SearchAction.SubmitCaptcha -> submitCaptcha(action)
                    is SearchAction.AddError -> onSearchError(action.error, action.onRetry)
                    is SearchAction.PlayAudio -> playAudio(action.audio)
                }
            }
        }

        viewModelScope.launch {
            combine(searchTriggerState, searchFilterState, ::SearchEvent)
                .debounce(SEARCH_DEBOUNCE_MILLIS)
                .collectLatest {
                    search(it)
                    onSearchEventChannel.send(it)
                }
        }

        listOf(audiosPager,artistsPager, albumsPager).forEach { pager ->
            pager.errors().watchForErrors(pager)
        }
    }

    private fun search(searchEvent: SearchEvent) {
        val (trigger, filter) = searchEvent
        val query = trigger.query
        val searchParams = SoundspotSearchParams(query, trigger.captchaSolution)
        val backends = filter.backends.joinToString { it.type }

        Timber.d("Searching with query=$query, backends=$backends")
        analytics.event("search", mapOf("query" to query, "backends" to backends))

        if (filter.hasAudios)
            audiosPager(ObservePagedSoundspotSearch.Params(searchParams))

        // don't send queries if backend can't handle empty queries
        if (query.isNotBlank()) {
            if (filter.hasArtists)
                artistsPager(ObservePagedSoundspotSearch.Params(searchParams.withTypes(BackendType.ARTISTS)))
            if (filter.hasAlbums)
                albumsPager(ObservePagedSoundspotSearch.Params(searchParams.withTypes(BackendType.ALBUMS)))
        }
    }

    private fun onSearchError(error: Throwable, onRetry: () -> Unit) = viewModelScope.launch {
        snackbarManager.addError(error = error, onRetry = onRetry)
    }

    /**
     * Queue given audio to play with current query as the queue.
     */
    private fun playAudio(audio: Audio) {
        val query = searchTriggerState.value.query
        when {
            else -> playbackConnection.playWithQuery(query, audio.id)
        }
    }

    /**
     * Sets search filter to only given backend if [action.selected] otherwise resets to [SearchFilter.DefaultBackends].
     */
    private fun selectBackendType(action: SearchAction.SelectBackendType) {
        analytics.event("search.selectBackend", mapOf("type" to action.backendType))
        searchFilterState.value = searchFilterState.value.copy(
            backends = when (action.selected) {
                true -> setOf(action.backendType)
                else -> SearchFilter.DefaultBackends
            }
        )
    }

    /**
     * Resets captcha error and triggers search with given captcha solution.
     */
    private fun submitCaptcha(action: SearchAction.SubmitCaptcha) {
        captchaError.value = null
        searchTriggerState.value = SearchTrigger(
            query = searchQueryState.value,
            captchaSolution = CaptchaSolution(
                action.captchaError.error.captchaId,
                action.captchaError.error.captchaIndex,
                action.solution
            )
        )
    }

    private fun Flow<Throwable>.watchForErrors(pager: ObservePagedSoundspotSearch<*>) = viewModelScope.launch { collectErrors(pager) }

    private suspend fun Flow<Throwable>.collectErrors(pager: ObservePagedSoundspotSearch<*>) = collectLatest { error ->
        Timber.e(error, "Collected error from a pager: $pager")
        when (error) {
            is ApiCaptchaError -> captchaError.value = error
        }
    }

    fun onSearchAction(action: SearchAction) = viewModelScope.launch {
        pendingActions.emit(action)
    }
}
