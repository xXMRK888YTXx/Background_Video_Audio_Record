package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.IsCanStartRecordAudioServiceUseCase

import com.xxmrk888ytxx.audiorecordservice.models.RecordAudioState
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordServiceManager.AudioRecordServiceManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.VideoRecordServiceManager.VideoRecordServiceManager
import com.xxmrk888ytxx.recordvideoservice.models.RecordVideoState
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class IsCanStartRecordAudioServiceUseCaseImpl @Inject constructor(
    private val videoRecordServiceManager: VideoRecordServiceManager
) : IsCanStartRecordAudioServiceUseCase {

    override suspend fun execute(): Boolean {
        return videoRecordServiceManager.currentRecordState.first() is RecordVideoState.Idle
    }
}