package com.xxmrk888ytxx.settingsscreen.models.DialogModels

import androidx.compose.runtime.Immutable
import com.xxmrk888ytxx.coreandroid.models.DualCameraPrimary

sealed class DualCameraPrimarySelectDialogState {
    @Immutable
    object Hidden : DualCameraPrimarySelectDialogState()

    @Immutable
    data class Showed(val currentSetup: DualCameraPrimary) : DualCameraPrimarySelectDialogState()
}
