package com.xxmrk888ytxx.settingsscreen.models.DialogModels

import com.xxmrk888ytxx.coreandroid.models.QuickTileCameraType

sealed interface VolumeKeyCameraSelectDialogState {
    data object Hidden : VolumeKeyCameraSelectDialogState
    data class Showed(val currentCameraType: QuickTileCameraType) : VolumeKeyCameraSelectDialogState
}
