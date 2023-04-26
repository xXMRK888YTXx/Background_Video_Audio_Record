package com.xxmrk888ytxx.audiorecordservice.models

sealed class RecordAudioState(open val recordDuration:Long) {
    object Idle : RecordAudioState(-1)

    data class Pause(override val recordDuration: Long) : RecordAudioState(recordDuration)

    data class Recording(override val recordDuration: Long) : RecordAudioState(recordDuration)
}
