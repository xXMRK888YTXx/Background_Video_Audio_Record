package com.xxmrk888ytxx.recordvideoscreen.contract

/**
 * [Ru]
 * Контракт для управлением записью
 */

/**
 * [En]
 * Contract for record management
 */
interface RecordVideoManageContract {

    suspend fun start()

    suspend fun pause()

    suspend fun resume()

    suspend fun stop()
}