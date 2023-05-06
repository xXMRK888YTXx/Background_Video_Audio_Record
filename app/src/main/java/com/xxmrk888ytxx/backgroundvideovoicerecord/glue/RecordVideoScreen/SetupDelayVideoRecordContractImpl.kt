package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.RecordVideoScreen

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.DelayStartRecordManager.DelayStartRecordManager
import com.xxmrk888ytxx.recordaudioscreen.contracts.SetupDelayAudioRecordContract
import com.xxmrk888ytxx.recordvideoscreen.contract.SetupDelayVideoRecordContract
import javax.inject.Inject

class SetupDelayVideoRecordContractImpl @Inject constructor(
    private val delayStartRecordManager: DelayStartRecordManager
) : SetupDelayVideoRecordContract {

    override suspend fun setDelayRecord(time: Long) {
        delayStartRecordManager.setDelayVideoRecord(time)
    }
}