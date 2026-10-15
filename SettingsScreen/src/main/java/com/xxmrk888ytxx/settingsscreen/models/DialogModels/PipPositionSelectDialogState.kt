package com.xxmrk888ytxx.settingsscreen.models.DialogModels

import androidx.compose.runtime.Immutable
import com.xxmrk888ytxx.coreandroid.models.PipPosition

sealed class PipPositionSelectDialogState {
    @Immutable
    object Hidden : PipPositionSelectDialogState()

    @Immutable
    data class Showed(val currentSetup: PipPosition) : PipPositionSelectDialogState()
}
