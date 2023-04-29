package com.xxmrk888ytxx.recordvideoservice

import com.xxmrk888ytxx.recordvideoservice.models.RecordVideoState
import kotlinx.coroutines.flow.Flow

interface RecordVideoServiceController {
    
    val currentState: Flow<RecordVideoState>
    
    fun startRecord()
    
    fun pauseRecord()
    
    fun resumeRecord()
    
    fun stopRecord()
    
}