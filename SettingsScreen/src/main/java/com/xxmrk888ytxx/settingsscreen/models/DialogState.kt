package com.xxmrk888ytxx.settingsscreen.models

import androidx.compose.runtime.Stable
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.CameraMaxQualitySelectDialogState
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.CameraTypeSelectDialogState

@Stable
data class DialogState(
    val cameraTypeSelectDialogState:CameraTypeSelectDialogState = CameraTypeSelectDialogState.Hidden,
    val cameraMaxQualitySelectDialogState: CameraMaxQualitySelectDialogState = CameraMaxQualitySelectDialogState.Hidden
)
