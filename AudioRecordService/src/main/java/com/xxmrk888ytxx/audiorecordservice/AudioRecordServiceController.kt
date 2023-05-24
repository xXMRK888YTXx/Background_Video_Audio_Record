package com.xxmrk888ytxx.audiorecordservice

import com.xxmrk888ytxx.audiorecordservice.models.RecordAudioState
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * [Ru]
 * Интерфейс для управления сервисом аудиозаписи
 */

/**
 * [En]
 * Interface for managing the audio recording service
 */
interface AudioRecordServiceController {

    /**
     * [Ru]
     * Запускает запись звука
     *
     * Установливает состояние в [RecordAudioState.Recording]
     */

    /**
     * [En]
     * Starts sound recording
     *
     * Sets the state in [RecordAudioState.Recording]
     */
    fun startRecord(recordFileOutput: File)

    /**
     * [Ru]
     * Приостановливает запись
     *
     * Установливает состояние в [RecordAudioState.Pause]
     */
    /**
     * [En]
     * Suspends recording
     *
     * Sets the state to [RecordAudioState.Pause]
     */
    fun pauseRecord()

    /**
     * [Ru]
     * Возобновляет запись, если та была ранее приостановлена вызовом метода [pauseRecord]
     *
     * Установливает состояние в [RecordAudioState.Recording]
     */

    /**
     * [En]
     * Resumes recording if it was previously paused by calling the [pauseRecord] method
     *
     * Sets state to [RecordAudioState.Recording]
     */
    fun resumeRecord()

    /**
     * [Ru]
     * Завершает запись. После вызова данного метода записанный файл будет сохранён путём вызова
     * метода [SaveAudioRecordStrategy.saveRecord]. После сохранения, сервис будет для повторной записи.
     *
     * Устанавливает состояние [RecordAudioState.Idle]
     *
     */

    /**
     * [En]
     * Ends recording. After calling this method, the recorded file will be saved by calling
     * method [SaveAudioRecordStrategy.saveRecord]. After saving, the service will be for re-recording.
     *
     * Sets the [RecordAudioState.Idle] state.
     *
     */
    fun stopRecord()

    /**
     * [Ru]
     * Возвращает текущее состояние
     */

    /**
     * [En]
     * Returns the current state
     */
    val currentState: Flow<RecordAudioState>
}