package com.xxmrk888ytxx.recordvideoservice

import com.xxmrk888ytxx.recordvideoservice.models.CameraType
import com.xxmrk888ytxx.recordvideoservice.models.RecordVideoState
import kotlinx.coroutines.flow.Flow
import java.io.File

interface RecordVideoServiceController {
    
    val currentState: Flow<RecordVideoState>
    
    fun startRecord(
        outputFile: File,
        secondaryOutputFile: File? = null,
        cameraTypeOverride: CameraType? = null
    )
    
    fun pauseRecord()
    
    fun resumeRecord()
    
    suspend fun stopRecord()
    
}