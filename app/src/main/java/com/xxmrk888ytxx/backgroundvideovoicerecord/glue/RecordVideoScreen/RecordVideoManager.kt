package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.RecordVideoScreen

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.IsCanStartRecordAudioServiceUseCase.IsCanStartRecordAudioServiceUseCase
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.IsCanStartRecordVideoServiceUseCase.IsCanStartRecordVideoServiceUseCase
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.VideoRecordServiceManager.VideoRecordServiceManager
import com.xxmrk888ytxx.recordvideoscreen.contract.RecordVideoManageContract
import com.xxmrk888ytxx.recordvideoscreen.contract.RecordVideoStateProviderContract
import com.xxmrk888ytxx.recordvideoscreen.exceptions.OtherRecordServiceStartedException
import com.xxmrk888ytxx.recordvideoscreen.models.RecordState
import com.xxmrk888ytxx.recordvideoservice.models.RecordVideoState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecordVideoManager @Inject constructor(
    private val recordVideoServiceManager: VideoRecordServiceManager,
    private val isCanStartRecordVideoServiceUseCase: IsCanStartRecordVideoServiceUseCase
) : RecordVideoManageContract,RecordVideoStateProviderContract {

    override suspend fun start() {
        if(!isCanStartRecordVideoServiceUseCase.execute()) {
            throw OtherRecordServiceStartedException()
        }
        recordVideoServiceManager.startRecord()
    }

    override suspend fun pause() {
        recordVideoServiceManager.pauseRecord()
    }

    override suspend fun resume() {
        recordVideoServiceManager.resumeRecord()
    }

    override suspend fun stop() {
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