package com.xxmrk888ytxx.settingsscreen.models.DialogModels

import androidx.compose.runtime.Immutable
import com.xxmrk888ytxx.settingsscreen.models.configs.CameraRotation

sealed class CameraRotationSelectDialogState {

    @Immutable
    object Hidden : CameraRotationSelectDialogState()

    @Immutable
    data class Showed(val initialCameraRotation:CameraRotation) : CameraRotationSelectDialogState()
}