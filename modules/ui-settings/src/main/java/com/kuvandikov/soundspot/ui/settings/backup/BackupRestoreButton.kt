/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.settings.backup

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import com.kuvandikov.base.util.CreateFileContract
import com.kuvandikov.common.compose.LocalIsPreviewMode
import com.kuvandikov.common.compose.previews.CombinedPreview
import com.kuvandikov.common.compose.rememberFlowWithLifecycle
import com.kuvandikov.soundspot.ui.settings.R
import com.kuvandikov.soundspot.ui.settings.SettingsLoadingButton
import com.kuvandikov.ui.theme.AppTheme

@Composable
internal fun BackupRestoreButton(
    modifier: Modifier = Modifier,
    isPreviewMode: Boolean = LocalIsPreviewMode.current,
) {
    when (isPreviewMode) {
        true -> BackupRestoreButtonPreview()
        false -> BackupRestoreButton(modifier, hiltViewModel())
    }
}

@Composable
private fun BackupRestoreButton(
    modifier: Modifier = Modifier,
    viewModel: BackupRestoreViewModel,
) {
    val viewState by rememberFlowWithLifecycle(viewModel.state)
    BackupRestoreButton(
        modifier = modifier,
        viewState = viewState,
        onBackupTo = viewModel::backupTo,
        onRestoreFrom = viewModel::restoreFrom,
    )
}

@Composable
private fun BackupRestoreButton(
    modifier: Modifier = Modifier,
    viewState: BackupRestoreViewState,
    onBackupTo: (Uri) -> Unit,
    onRestoreFrom: (Uri) -> Unit,
) {
    val backupOutputFilePickerLauncher = rememberLauncherForActivityResult(contract = CreateFileContract(BACKUP_FILE_PARAMS)) {
        if (it != null) onBackupTo(it)
    }
    val restoreInputFilePickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
        if (it != null) onRestoreFrom(it)
    }

    FlowRow(
        mainAxisAlignment = FlowMainAxisAlignment.End,
        mainAxisSpacing = AppTheme.specs.paddingSmall,
        mainAxisSize = SizeMode.Expand,
        modifier = modifier,
    ) {
        SettingsLoadingButton(
            isLoading = viewState.isBackingUp,
            text = stringResource(R.string.settings_database_backup),
            onClick = { backupOutputFilePickerLauncher.launch(arrayOf(BACKUP_FILE_PARAMS.fileMimeType)) }
        )
        SettingsLoadingButton(
            isLoading = viewState.isRestoring,
            text = stringResource(R.string.settings_database_restore),
            onClick = { restoreInputFilePickerLauncher.launch(BACKUP_FILE_PARAMS.fileMimeType) }
        )
    }
}

@CombinedPreview
@Composable
private fun BackupRestoreButtonPreview(modifier: Modifier = Modifier) {
    BackupRestoreButton(
        modifier = modifier,
        viewState = BackupRestoreViewState.Empty,
        onBackupTo = {},
        onRestoreFrom = {}
    )
}
