package com.xxmrk888ytxx.recordaudioscreen.models

import androidx.compose.runtime.Stable

@Stable
data class DialogState(
    val isPermissionDialogVisible:Boolean = false,
    val isIgnoreBatteryOptimizationDialogVisible:Boolean = false
)