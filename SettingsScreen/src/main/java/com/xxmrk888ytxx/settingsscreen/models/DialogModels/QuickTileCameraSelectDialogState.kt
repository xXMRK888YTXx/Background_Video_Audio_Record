package com.xxmrk888ytxx.settingsscreen.models.DialogModels

import com.xxmrk888ytxx.coreandroid.models.QuickTileCameraType

sealed interface QuickTileCameraSelectDialogState {
    data object Hidden : QuickTileCameraSelectDialogState
    data class Showed(val currentCameraType: QuickTileCameraType) : QuickTileCameraSelectDialogState
}
