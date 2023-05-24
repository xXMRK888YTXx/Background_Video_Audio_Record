package com.xxmrk888ytxx.storagescreen.MediaFileItem.models

import androidx.annotation.IdRes
import androidx.compose.runtime.Immutable

@Immutable
data class MediaFileButton(
    @IdRes val icon:Int,
    val onClick:() -> Unit
)
