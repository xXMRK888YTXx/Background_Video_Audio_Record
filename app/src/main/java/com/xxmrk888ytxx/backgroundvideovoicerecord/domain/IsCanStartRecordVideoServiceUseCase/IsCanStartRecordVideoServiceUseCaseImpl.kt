package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.IsCanStartRecordVideoServiceUseCase

import com.xxmrk888ytxx.audiorecordservice.models.RecordAudioState
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordServiceManager.AudioRecordServiceManager
import com.xxmrk888ytxx.recordvideoservice.models.RecordVideoState
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class IsCanStartRecordVideoServiceUseCaseImpl @Inject constructor(
    private val audioRecordServiceManager: AudioRecordServiceManager
) : IsCanStartRecordVideoServiceUseCase {
    override suspend fun execute(): Boolean {
        return audioRecordServiceManager.currentRecordState.first() is RecordAudioState.Idle
    }
}