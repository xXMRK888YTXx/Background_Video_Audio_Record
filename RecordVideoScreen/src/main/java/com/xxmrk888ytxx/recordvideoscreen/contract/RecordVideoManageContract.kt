package com.xxmrk888ytxx.recordvideoscreen.contract

/**
 * [Ru]
 * Контракт для управлением записью
 */

/**
 * [En]
 * Contract for record management
 */
import com.xxmrk888ytxx.recordvideoscreen.models.DualCameraSaveOption
import kotlinx.coroutines.flow.Flow

interface RecordVideoManageContract {

    suspend fun start()

    suspend fun pause()

    suspend fun resume()

    suspend fun stop()

    val hasPendingDualRecord: Flow<Boolean>

    suspend fun saveDualRecord(option: DualCameraSaveOption)
}