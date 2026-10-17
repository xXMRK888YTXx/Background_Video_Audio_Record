package com.xxmrk888ytxx.settingsscreen.models.DialogModels

sealed interface AdbPermissionDialogState {
    data object Hidden : AdbPermissionDialogState
    data object Showed : AdbPermissionDialogState
}
