package com.xxmrk888ytxx.settingsscreen.models.DialogModels

import androidx.compose.runtime.Immutable
import com.xxmrk888ytxx.settingsscreen.models.configs.CameraType


sealed class CameraTypeSelectDialogState {
    @Immutable
    object Hidden : CameraTypeSelectDialogState()

    @Immutable
    data class Showed(val currentSelected:CameraType) : CameraTypeSelectDialogState()
}
