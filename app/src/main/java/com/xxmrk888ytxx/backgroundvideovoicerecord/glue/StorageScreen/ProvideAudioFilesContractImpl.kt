package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordRepository.AudioRecordRepository
import com.xxmrk888ytxx.storagescreen.contracts.ProvideAudioFilesContract
import com.xxmrk888ytxx.storagescreen.models.AudioFileModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProvideAudioFilesContractImpl @Inject constructor(
    private val audioRecordRepository: AudioRecordRepository
) : ProvideAudioFilesContract {

    override val files: Flow<List<AudioFileModel>> = audioRecordRepository.fileList.map { list ->
        list.map { AudioFileModel(it.id,it.duration,it.created) }
    }
}