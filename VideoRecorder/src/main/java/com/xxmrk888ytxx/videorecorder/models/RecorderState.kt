package com.xxmrk888ytxx.videorecorder.models

import java.time.Duration

sealed class RecorderState(open val currentRecordDuration: Long) {

    object Idle : RecorderState(0)

    data class Recording(override val currentRecordDuration: Long) : RecorderState(currentRecordDuration)

    data class Paused(override val currentRecordDuration: Long) : RecorderState(currentRecordDuration)

    object Destroyed : RecorderState(-1)
}
