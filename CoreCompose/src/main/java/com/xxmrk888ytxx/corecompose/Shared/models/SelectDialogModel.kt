package com.xxmrk888ytxx.corecompose.Shared.models

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Immutable
data class SelectDialogModel(
    val title:String,
    val isSelected:Boolean,
    val onClick:() -> Unit
)
