package com.xxmrk888ytxx.videorecorder.models



/**
 * [Ru]
 * Состояния видео записи
 *
 * @param currentRecordDuration - время записи
 */

/**
 * [En]
 * Video recording states
 *
 * @param currentRecordDuration - recording time
 */
sealed class RecorderState(open val currentRecordDuration: Long) {

    /**
     * [Ru]
     * В этом состоянии, рекордер готов для записи и ожидает команд
     */

    /**
     * [En]
     * In this state, the recorder is ready for recording and waiting for commands
     */
    object Idle : RecorderState(0)

    /**
     * [Ru]
     * В данное состояние означает, что в данный момент идет запись
     */

    /**
     * [En]
     * In this state means that the recording is in progress
     */
    data class Recording(override val currentRecordDuration: Long) : RecorderState(currentRecordDuration)

    /**
     * [Ru]
     * Это состояние означает, что запись началась, но в данный момент приостановлена
     */

    /**
     * [En]
     * This state means that recording has started, but is currently paused
     */
    data class Paused(override val currentRecordDuration: Long) : RecorderState(currentRecordDuration)

    /**
     * [Ru]
     * Данное состояние означает, что рекордер уничтожен, и более не может использоваться для записи
     */

    /**
     * [En]
     * This state means that the recorder has been destroyed and can no longer be used for recording
     */
    object Destroyed : RecorderState(-1)
}
