package com.xxmrk888ytxx.settingsscreen.models

import androidx.compose.runtime.Stable
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.CameraTypeSelectDialogState

@Stable
data class DialogState(
    val isCameraTypeSelectDialogVisible:CameraTypeSelectDialogState = CameraTypeSelectDialogState.Hidden
)
