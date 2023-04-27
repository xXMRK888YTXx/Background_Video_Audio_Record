package com.xxmrk888ytxx.storagescreen.contracts

import com.xxmrk888ytxx.storagescreen.models.Player

interface PlayerFactoryContract {

    fun create(onLockScreen:() -> Unit,onUnlockScreen:() -> Unit) : Player
}