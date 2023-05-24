package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.VideoStorageList

import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoRecordRepository.VideoRecordRepository
import com.xxmrk888ytxx.storagescreen.VideoStorageList.contract.DeleteVideoFileContract
import javax.inject.Inject

class DeleteVideoFileContractImpl @Inject constructor(
    private val videoRecordRepository: VideoRecordRepository
) : DeleteVideoFileContract {

    override suspend fun removeFile(id: Long) {
        videoRecordRepository.removeFile(id)
    }
}