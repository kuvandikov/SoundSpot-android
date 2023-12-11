/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.ui.downloader

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.kuvandikov.datmusic.downloader.Downloader

@HiltViewModel
internal class DownloaderViewModel @Inject constructor(val downloader: Downloader) : ViewModel()
