package com.xxmrk888ytxx.recordvideoscreen.contract

import com.xxmrk888ytxx.recordvideoscreen.models.RecordState
import kotlinx.coroutines.flow.Flow

/**
 * [Ru]
 * Контракт для предоставления текущего состояния записи
 */
/**
 * [En]
 * Contract to provide the current state of the record
 */
interface RecordVideoStateProviderContract {

    val currentState:Flow<RecordState>
}