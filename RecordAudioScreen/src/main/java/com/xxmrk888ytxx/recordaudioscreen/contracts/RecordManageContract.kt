package com.xxmrk888ytxx.recordaudioscreen.contracts

/**
 * [Ru]
 * Контракт для управлением записью
 */

/**
 * [En]
 * Contract for record management
 */
interface RecordManageContract {

    suspend fun start()

    suspend fun pause()

    suspend fun resume()

    suspend fun stop()
}