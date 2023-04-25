package com.xxmrk888ytxx.recordaudioscreen.contracts

import com.xxmrk888ytxx.recordaudioscreen.models.RecordState
import kotlinx.coroutines.flow.Flow

interface RecordStateProviderContract {

    val currentState:Flow<RecordState>
}