package com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts

import com.xxmrk888ytxx.storagescreen.AudioStorageList.models.Player

interface PlayerFactoryContract {

    fun create(onLockScreen:() -> Unit,onUnlockScreen:() -> Unit) : Player
}