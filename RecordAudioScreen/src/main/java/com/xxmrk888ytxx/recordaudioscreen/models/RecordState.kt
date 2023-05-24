package com.xxmrk888ytxx.recordaudioscreen.models

sealed class RecordState(open val recordDuration:Long) {

    object Idle : RecordState(0L)

    data class Recording(override val recordDuration:Long) : RecordState(recordDuration)

    data class Paused(override val recordDuration:Long) : RecordState(recordDuration)
}
