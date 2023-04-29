package com.xxmrk888ytxx.recordvideoscreen.models

import java.time.Duration

sealed class RecordState(open val currentDuration: Long) {

    object Idle : RecordState(0)

    data class Recording(override val currentDuration: Long) : RecordState(currentDuration)

    data class Pause(override val currentDuration: Long) : RecordState(currentDuration)
}
