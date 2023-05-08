package com.xxmrk888ytxx.settingsscreen

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.xxmrk888ytxx.settingsscreen.models.SettingsParamType

@SuppressLint("ResourceType")
@Composable
internal fun cameraParams(settingsViewModel: SettingsViewModel) : List<SettingsParamType> {

    return listOf(
        SettingsParamType.Button(
            stringResource(R.string.Camera_type),
            icon = R.drawable.camera_type,
            onClick = settingsViewModel::showCameraTypeSelectDialog
        )
    )
}