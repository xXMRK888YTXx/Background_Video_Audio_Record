package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.AudioStorageList

import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.AudioRecordRepository.AudioRecordRepository
import com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts.ProvideAudioFilesContract
import com.xxmrk888ytxx.storagescreen.AudioStorageList.models.AudioFileModel
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