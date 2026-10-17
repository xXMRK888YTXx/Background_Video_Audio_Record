package com.xxmrk888ytxx.settingsscreen.models.DialogModels

import com.xxmrk888ytxx.coreandroid.models.QuickTileAction

sealed interface QuickTileActionSelectDialogState {
    data object Hidden : QuickTileActionSelectDialogState
    data class Showed(val currentAction: QuickTileAction) : QuickTileActionSelectDialogState
}
