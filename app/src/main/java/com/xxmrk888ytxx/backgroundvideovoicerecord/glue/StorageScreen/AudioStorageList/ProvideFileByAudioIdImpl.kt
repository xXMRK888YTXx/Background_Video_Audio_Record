package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.AudioStorageList

import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.AudioRecordRepository.AudioRecordRepository
import com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts.ProvideFileByAudioId
import java.io.File
import javax.inject.Inject

class ProvideFileByAudioIdImpl @Inject constructor(
    private val audioRecordRepository: AudioRecordRepository
) : ProvideFileByAudioId {
    override suspend fun provide(id: Int): File? {
        return audioRecordRepository.getFileById(id)
    }
}