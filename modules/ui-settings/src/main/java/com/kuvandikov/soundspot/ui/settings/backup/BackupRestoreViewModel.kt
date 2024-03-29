/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.settings.backup

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import com.kuvandikov.base.ui.SnackbarManager
import com.kuvandikov.base.util.Analytics
import com.kuvandikov.base.util.CreateFileContract
import com.kuvandikov.base.util.extensions.stateInDefault
import com.kuvandikov.base.util.toUiMessage
import com.kuvandikov.soundspot.data.interactors.backup.CreateSoundspotBackupToFile
import com.kuvandikov.soundspot.data.interactors.backup.RestoreSoundspotFromFile
import com.kuvandikov.soundspot.playback.PlaybackConnection
import com.kuvandikov.soundspot.ui.settings.R
import com.kuvandikov.domain.models.Fail
import com.kuvandikov.domain.models.Success
import com.kuvandikov.i18n.UiMessage

internal val BACKUP_FILE_PARAMS = CreateFileContract.Params(
    suggestedName = "soundspot-backup",
    fileExtension = "json",
    fileMimeType = "application/json"
)

@HiltViewModel
internal class BackupRestoreViewModel @Inject constructor(
    private val backupToFile: CreateSoundspotBackupToFile,
    private val restoreFromFile: RestoreSoundspotFromFile,
    private val snackbarManager: SnackbarManager,
    private val analytics: Analytics,
    private val playbackConnection: PlaybackConnection,
) : ViewModel() {

    private val isBackingUp = MutableStateFlow(false)
    private val isRestoring = MutableStateFlow(false)

    val state = combine(isBackingUp, isRestoring, ::BackupRestoreViewState)
        .stateInDefault(viewModelScope, BackupRestoreViewState.Empty)

    init {
        viewModelScope.launch {
            restoreFromFile.warnings.collectLatest { snackbarManager.addMessage(it.toUiMessage()) }
        }
    }

    fun backupTo(file: Uri) = viewModelScope.launch {
        analytics.event("settings.db.backup")
        playbackConnection.transportControls?.stop()
        backupToFile(file).collectLatest {
            isBackingUp.value = it.isLoading
            when (it) {
                is Fail -> snackbarManager.addMessage(it.error.toUiMessage())
                is Success -> snackbarManager.addMessage(UiMessage.Resource(R.string.settings_database_backup_complete))
                else -> Unit
            }
        }
    }

    fun restoreFrom(file: Uri) = viewModelScope.launch {
        analytics.event("settings.db.restore")
        playbackConnection.transportControls?.stop()
        restoreFromFile(file).collectLatest {
            isRestoring.value = it.isLoading
            when (it) {
                is Fail -> snackbarManager.addMessage(it.error.toUiMessage())
                is Success -> snackbarManager.addMessage(UiMessage.Resource(R.string.settings_database_restore_complete))
                else -> Unit
            }
        }
    }
}
