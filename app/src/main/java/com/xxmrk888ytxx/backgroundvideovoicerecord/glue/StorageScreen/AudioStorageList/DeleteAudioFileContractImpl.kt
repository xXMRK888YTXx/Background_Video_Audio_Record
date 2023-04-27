package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.AudioStorageList

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordRepository.AudioRecordRepository
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordRepository.AudioRecordRepositoryImpl
import com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts.DeleteAudioFileContract
import javax.inject.Inject

class DeleteAudioFileContractImpl @Inject constructor(
    private val audioRecordRepository: AudioRecordRepository
) : DeleteAudioFileContract {
    override suspend fun execute(id: Int) {
        audioRecordRepository.removeFile(id)
    }
}