package com.xxmrk888ytxx.audiorecordservice

import kotlinx.coroutines.CoroutineScope
import java.io.File

/**
 * [Ru]
 * Стратегия сохранения файла, после окончания его записи
 */

/**
 * [En]
 * Strategy of saving the file, after the end of its recording
 */
interface SaveAudioRecordStrategy {

    /**
     * [Ru]
     * [CoroutineScope] на котором будет выполняться эта стратегия
     */

    /**
     * [En]
     * [CoroutineScope] on which this strategy will be executed
     */
    val scope: CoroutineScope

    /**
     * [Ru]
     * Метод для выполния стратегии
     *
     * @param recordedFile - расположение записаного файла
     */
    /**
     * [En]
     * Method for executing the strategy
     *
     * @param recordedFile - location of recorded file
     */
    suspend fun saveRecord(recordedFile: File)
}