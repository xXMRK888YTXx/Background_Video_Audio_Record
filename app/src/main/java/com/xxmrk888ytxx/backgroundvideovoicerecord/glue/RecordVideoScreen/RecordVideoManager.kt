package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.RecordVideoScreen

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.VideoRecordServiceManager.VideoRecordServiceManager
import com.xxmrk888ytxx.recordvideoscreen.contract.RecordVideoManageContract
import com.xxmrk888ytxx.recordvideoscreen.contract.RecordVideoStateProviderContract
import com.xxmrk888ytxx.recordvideoscreen.models.RecordState
import com.xxmrk888ytxx.recordvideoservice.models.RecordVideoState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecordVideoManager @Inject constructor(
    private val recordVideoServiceManager: VideoRecordServiceManager
) : RecordVideoManageContract,RecordVideoStateProviderContract {

    override fun start() {
        recordVideoServiceManager.startRecord()
    }

    override fun pause() {
        recordVideoServiceManager.pauseRecord()
    }

    override fun resume() {
        recordVideoServiceManager.resumeRecord()
    }

    override fun stop() {
        recordVideoServiceManager.stopRecord()
    }

    override val currentState: Flow<RecordState> = recordVideoServiceManager
        .currentRecordState
        .map { state ->
            when(state) {
                is RecordVideoState.Idle -> RecordState.Idle

                is RecordVideoState.Recording -> RecordState.Recording(state.recordDuration)

                is RecordVideoState.Pause -> RecordState.Pause(state.recordDuration)
            }
        }
}