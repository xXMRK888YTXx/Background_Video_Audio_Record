package com.xxmrk888ytxx.storagescreen.MediaFileItem.models

import androidx.annotation.IdRes
import androidx.compose.runtime.Immutable

@Immutable
data class MediaFileButton(
    @param:IdRes val icon:Int,
    val onClick:() -> Unit
)
