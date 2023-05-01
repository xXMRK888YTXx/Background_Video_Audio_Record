package com.xxmrk888ytxx.recordvideoservice

import com.xxmrk888ytxx.recordvideoservice.models.RecordVideoState
import kotlinx.coroutines.flow.Flow
import java.io.File

interface RecordVideoServiceController {
    
    val currentState: Flow<RecordVideoState>
    
    fun startRecord(outputFile: File)
    
    fun pauseRecord()
    
    fun resumeRecord()
    
    fun stopRecord()
    
}