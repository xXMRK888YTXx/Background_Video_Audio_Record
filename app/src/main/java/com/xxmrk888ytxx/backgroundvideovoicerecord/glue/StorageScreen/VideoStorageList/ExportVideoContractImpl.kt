package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.VideoStorageList

import android.net.Uri
import com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.ExportFileUseCase.ExportFileUseCase
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoRecordRepository.VideoRecordRepository
import com.xxmrk888ytxx.storagescreen.VideoStorageList.contract.ExportVideoContract
import java.io.FileNotFoundException
import javax.inject.Inject

class ExportVideoContractImpl @Inject constructor(
    private val exportFileUseCase: ExportFileUseCase,
    private val videoRecordRepository: VideoRecordRepository
) : ExportVideoContract {
    override suspend fun exportVideo(videoId: Long, path: Uri): Result<Unit> {
        val videoFile = videoRecordRepository.getFileById(videoId) ?: return Result.failure(FileNotFoundException())

        return exportFileUseCase.export(videoFile,path)
    }
}