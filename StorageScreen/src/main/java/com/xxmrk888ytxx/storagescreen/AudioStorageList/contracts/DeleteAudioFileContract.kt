package com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts

/**
 * [Ru]
 * Контракт для удаления, записаного файла
 */

/**
 * [En]
 * Contract for deletion, recorded file
 */
interface DeleteAudioFileContract {

    suspend fun execute(id:Int)
}