package com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts

import java.io.File

/**
 * [Ru]
 *  Контрат для предоставления [File], записаного аудио файла
 */
/**
 * [En]
 * Contrast to provide [File], the recorded audio file
 */
interface ProvideFileByAudioId {

    suspend fun provide(id:Int) : File?
}