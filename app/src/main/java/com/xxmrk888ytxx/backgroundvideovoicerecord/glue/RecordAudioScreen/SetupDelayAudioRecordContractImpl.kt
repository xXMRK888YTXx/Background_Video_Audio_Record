package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.RecordAudioScreen

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.DelayStartRecordManager.DelayStartRecordManager
import com.xxmrk888ytxx.recordaudioscreen.contracts.SetupDelayAudioRecordContract
import javax.inject.Inject

class SetupDelayAudioRecordContractImpl @Inject constructor(
    private val delayStartRecordManager: DelayStartRecordManager
) : SetupDelayAudioRecordContract {

    override suspend fun setDelayRecord(time: Long) {
        delayStartRecordManager.setDelayAudioRecord(time)
    }
}