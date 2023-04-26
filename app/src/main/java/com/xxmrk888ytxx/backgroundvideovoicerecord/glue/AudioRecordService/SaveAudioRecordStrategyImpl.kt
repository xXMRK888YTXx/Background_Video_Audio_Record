package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.AudioRecordService

import android.content.Context
import com.xxmrk888ytxx.audiorecordservice.SaveAudioRecordStrategy
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.qualifiers.ApplicationScopeQualifier
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordRepository.AudioRecordRepository
import kotlinx.coroutines.CoroutineScope
import java.io.File
import javax.inject.Inject

class SaveAudioRecordStrategyImpl @Inject constructor(
    private val context:Context,
    @ApplicationScopeQualifier override val scope: CoroutineScope,
    private val audioRecordRepository: AudioRecordRepository
) : SaveAudioRecordStrategy {

    override suspend fun saveRecord(recordedFile: File) {
        audioRecordRepository.addFile(recordedFile)
    }
}
