package com.xxmrk888ytxx.settingsscreen

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.xxmrk888ytxx.settingsscreen.models.SettingsParamType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@SuppressLint("ResourceType")
@Composable
internal fun cameraParams(settingsViewModel: SettingsViewModel) : ImmutableList<SettingsParamType> {

    return persistentListOf(
        SettingsParamType.Button(
            stringResource(R.string.Camera_type),
            icon = R.drawable.camera_type,
            onClick = settingsViewModel::showCameraTypeSelectDialog
        ),
        SettingsParamType.Button(
            stringResource(R.string.Record_quality),
            icon = R.drawable.camera_type,
            onClick = settingsViewModel::showCameraMaxQualitySelectDialogState
        ),
    )
}