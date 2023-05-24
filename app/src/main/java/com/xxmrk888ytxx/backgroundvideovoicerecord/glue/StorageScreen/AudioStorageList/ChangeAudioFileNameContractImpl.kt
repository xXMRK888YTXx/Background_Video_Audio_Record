package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.AudioStorageList

import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.AudioFileNameRepository.AudioFileNameRepository
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.AudioFileNameRepository.models.AudioNameModel
import com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts.ChangeAudioFileNameContract
import javax.inject.Inject

class ChangeAudioFileNameContractImpl @Inject constructor(
    private val audioFileNameRepository: AudioFileNameRepository
) : ChangeAudioFileNameContract {

    override suspend fun changeFileName(id: Long, newName: String) {
        audioFileNameRepository.insertName(AudioNameModel(id,newName))
    }
}