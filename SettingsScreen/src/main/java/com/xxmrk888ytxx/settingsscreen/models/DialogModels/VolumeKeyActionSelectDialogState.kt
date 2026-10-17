package com.xxmrk888ytxx.settingsscreen.models.DialogModels

import com.xxmrk888ytxx.coreandroid.models.VolumeKeyAction

sealed interface VolumeKeyActionSelectDialogState {
    data object Hidden : VolumeKeyActionSelectDialogState
    data class Showed(val currentAction: VolumeKeyAction) : VolumeKeyActionSelectDialogState
}
