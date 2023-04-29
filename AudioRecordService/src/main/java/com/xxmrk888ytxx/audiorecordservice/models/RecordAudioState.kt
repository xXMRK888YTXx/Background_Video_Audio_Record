package com.xxmrk888ytxx.audiorecordservice.models

/**
 * [Ru]
 * Сотояния сервиса для аудио записи
 *
 * @param recordDuration - время которое, уже записывает сервис
 */

/**
 * [En]
 * Service states for audio recording
 *
 * @param recordDuration - time the service is already recording
 */
sealed class RecordAudioState(open val recordDuration:Long) {

    /**
     * [Ru]
     * В этом состоянии, сервис готов для записи и ожидает команд
     */

    /**
     * [En]
     * In this state, the service is ready for recording and waiting for commands
     */
    object Idle : RecordAudioState(-1)

    /**
     * [Ru]
     * Это состояние означает, что запись началась, но в данный момент приостановлена
     */

    /**
     * [En]
     * This state means that recording has started, but is currently paused
     */
    data class Pause(override val recordDuration: Long) : RecordAudioState(recordDuration)

        /**
         * [Ru]
         * В данное состояние означает, что в данный момент идет запись
         */

    /**
     * [En]
     * In this state means that the recording is in progress
     */
    data class Recording(override val recordDuration: Long) : RecordAudioState(recordDuration)
}