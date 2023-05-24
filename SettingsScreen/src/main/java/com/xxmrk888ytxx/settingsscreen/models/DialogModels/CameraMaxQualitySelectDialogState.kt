package com.xxmrk888ytxx.settingsscreen.models.DialogModels

import com.xxmrk888ytxx.settingsscreen.models.configs.CameraMaxQuality

sealed class CameraMaxQualitySelectDialogState {

    object Hidden : CameraMaxQualitySelectDialogState()

    data class Showed(val currentSetup:CameraMaxQuality) : CameraMaxQualitySelectDialogState()

}