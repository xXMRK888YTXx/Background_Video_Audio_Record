package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.VideoStorageList

import androidx.core.net.toUri
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoRecordRepository.VideoRecordRepository
import com.xxmrk888ytxx.coreandroid.Navigator
import com.xxmrk888ytxx.storagescreen.VideoStorageList.contract.OpenVideoContract
import javax.inject.Inject

class OpenVideoContactImpl @Inject constructor(
    private val videoRecordRepository: VideoRecordRepository
) : OpenVideoContract {

    override suspend fun openVideoContract(videoId: Long, navigator: Navigator) {
        val file = videoRecordRepository.getFileById(videoId) ?: return

        navigator.toVideoPlayerScreen(file.toUri())
    }
}