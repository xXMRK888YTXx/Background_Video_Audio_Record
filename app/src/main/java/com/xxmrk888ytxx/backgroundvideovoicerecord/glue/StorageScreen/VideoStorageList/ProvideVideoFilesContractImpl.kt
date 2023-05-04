package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.VideoStorageList

import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoRecordRepository.VideoRecordRepository
import com.xxmrk888ytxx.storagescreen.VideoStorageList.contract.ProvideVideoFilesContract
import com.xxmrk888ytxx.storagescreen.VideoStorageList.models.VideoFileModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProvideVideoFilesContractImpl @Inject constructor(
    private val videoRecordRepository: VideoRecordRepository
) : ProvideVideoFilesContract {

    override val videoFiles: Flow<List<VideoFileModel>> = videoRecordRepository.fileList.map { list ->
        list.map { VideoFileModel(it.id,it.duration,it.created,it.name) }
    }
}