package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.RecordAudioScreen

import com.xxmrk888ytxx.audiorecordservice.models.RecordAudioState
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordServiceManager.AudioRecordServiceManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.IsCanStartRecordAudioServiceUseCase.IsCanStartRecordAudioServiceUseCase
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.IsCanStartRecordVideoServiceUseCase.IsCanStartRecordVideoServiceUseCase
import com.xxmrk888ytxx.recordaudioscreen.contracts.RecordManageContract
import com.xxmrk888ytxx.recordaudioscreen.contracts.RecordStateProviderContract
import com.xxmrk888ytxx.recordaudioscreen.exceptions.OtherRecordStartedException
import com.xxmrk888ytxx.recordaudioscreen.models.RecordState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecordAudioManager @Inject constructor(
    private val audioRecordServiceManager: AudioRecordServiceManager,
    private val isCanStartRecordVideoServiceUseCase: IsCanStartRecordAudioServiceUseCase
) : RecordStateProviderContract,RecordManageContract {
    override suspend fun start() {
        if(!isCanStartRecordVideoServiceUseCase.execute()) {
            throw OtherRecordStartedException()
        }
        audioRecordServiceManager.startRecord()
    }

    override suspend fun pause() {
        audioRecordServiceManager.pauseRecord()
    }

    override suspend fun resume() {
        audioRecordServiceManager.resumeRecord()
    }

    override suspend fun stop() {
        audioRecordServiceManager.stopRecord()
    }

    override val currentState: Flow<RecordState> = audioRecordServiceManager.currentRecordState.map {
        when(it) {
            is RecordAudioState.Idle -> RecordState.Idle

            is RecordAudioState.Recording -> RecordState.Recording(it.recordDuration)

            is RecordAudioState.Pause -> RecordState.Paused(it.recordDuration)
        }
    }
}