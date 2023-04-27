package com.xxmrk888ytxx.storagescreen.AudioStorageList.models

import kotlinx.coroutines.flow.Flow
import java.io.File

interface Player {

    fun prepare(file: File)

    fun play()

    fun pause()

    fun stop()

    fun destroy()

    fun seekTo(time:Long)

    val isDestroyed:Boolean

    val isPrepare:Boolean

    val state:Flow<PlayerState>
}