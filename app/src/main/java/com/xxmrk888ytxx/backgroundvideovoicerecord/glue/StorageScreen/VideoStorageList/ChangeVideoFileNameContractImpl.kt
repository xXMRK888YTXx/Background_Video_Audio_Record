package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.VideoStorageList

import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoFileNameRepository.VideoFileNameRepository
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoFileNameRepository.models.VideoNameModel
import com.xxmrk888ytxx.storagescreen.VideoStorageList.contract.ChangeVideoFileNameContract
import javax.inject.Inject

class ChangeVideoFileNameContractImpl @Inject constructor(
    private val videoFileNameRepository: VideoFileNameRepository
) : ChangeVideoFileNameContract {

    override suspend fun changeFileName(id: Long, newName: String) {
        videoFileNameRepository.insertName(VideoNameModel(id,newName))
    }
}