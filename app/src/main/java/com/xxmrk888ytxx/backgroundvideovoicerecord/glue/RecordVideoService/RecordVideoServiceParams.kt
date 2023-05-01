package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.RecordVideoService

import com.xxmrk888ytxx.recordvideoservice.RecordVideoParams
import com.xxmrk888ytxx.recordvideoservice.SaveRecordedVideoStrategy
import javax.inject.Inject

class RecordVideoServiceParams @Inject constructor (
    override val saveRecordedVideoStrategy: SaveRecordedVideoStrategy
) : RecordVideoParams