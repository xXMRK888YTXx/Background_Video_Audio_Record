package com.xxmrk888ytxx.storagescreen.models

import androidx.annotation.IdRes

data class StorageType(
    @IdRes val label:Int,
    @IdRes val icon:Int,
    val onClick:() -> Unit
)
