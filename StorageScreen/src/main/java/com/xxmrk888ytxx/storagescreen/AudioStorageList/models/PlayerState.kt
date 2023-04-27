package com.xxmrk888ytxx.storagescreen.AudioStorageList.models

sealed class PlayerState(open val currentDuration: Long) {

    object Idle : PlayerState(0)

    data class Play(override val currentDuration: Long) : PlayerState(currentDuration)

    data class Pause(override val currentDuration: Long) : PlayerState(currentDuration)

    object Destroy : PlayerState(-1)
}

