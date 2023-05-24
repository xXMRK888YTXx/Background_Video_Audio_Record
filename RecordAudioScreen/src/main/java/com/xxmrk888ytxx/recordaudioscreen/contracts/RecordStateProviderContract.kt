package com.xxmrk888ytxx.recordaudioscreen.contracts

import com.xxmrk888ytxx.recordaudioscreen.models.RecordState
import kotlinx.coroutines.flow.Flow

/**
 * [Ru]
 * Контракт для предоставления текущего состояния записи
 */
/**
 * [En]
 * Contract to provide the current state of the record
 */
interface RecordStateProviderContract {

    val currentState:Flow<RecordState>
}