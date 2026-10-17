package com.xxmrk888ytxx.settingsscreen.models.DialogModels

sealed class SetPinDialogState {
    object Hidden : SetPinDialogState()
    data class Showed(val isChangeMode: Boolean = false) : SetPinDialogState()
}
