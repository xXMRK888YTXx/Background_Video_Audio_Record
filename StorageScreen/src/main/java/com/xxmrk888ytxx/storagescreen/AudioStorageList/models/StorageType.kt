package com.xxmrk888ytxx.storagescreen.AudioStorageList.models

import androidx.annotation.IdRes

data class StorageType(
    @param:IdRes val label:Int,
    @param:IdRes val icon:Int,
    val onClick:() -> Unit
)
