package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.VideoStorageList

import android.content.Context
import android.net.Uri
import com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.writeFileToExternalStorageUseCase.WriteFileToExternalStorageUseCase
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoRecordRepository.VideoRecordRepository
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.videoProcessing.DualVideoCombiner
import com.xxmrk888ytxx.coreandroid.models.DualCameraSaveOption
import com.xxmrk888ytxx.storagescreen.VideoStorageList.contract.ExportVideoContract
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject

import kotlinx.coroutines.flow.first

class ExportVideoContractImpl @Inject constructor(
    private val context: Context,
    private val writeFileToExternalStorageUseCase: WriteFileToExternalStorageUseCase,
    private val videoRecordRepository: VideoRecordRepository,
    private val dualVideoCombiner: DualVideoCombiner,
    private val dualCameraSettingsManager: com.xxmrk888ytxx.backgroundvideovoicerecord.domain.dualCameraSettings.DualCameraSettingsManager
) : ExportVideoContract {

    override suspend fun exportVideo(videoId: Long, path: Uri): Result<Unit> {
        val videoFile = videoRecordRepository.getFileById(videoId) ?: return Result.failure(FileNotFoundException())
        return writeFileToExternalStorageUseCase.write(videoFile, path)
    }

    override suspend fun exportDualVideo(
        videoId: Long,
        path: Uri,
        option: DualCameraSaveOption
    ): Result<Unit> {
        val primaryFile = videoRecordRepository.getFileById(videoId) ?: return Result.failure(FileNotFoundException())
        val secondaryFile = videoRecordRepository.getSecondaryFileById(videoId)

        return when (option) {
            DualCameraSaveOption.BACK_ONLY -> {
                writeFileToExternalStorageUseCase.write(primaryFile, path)
            }
            DualCameraSaveOption.FRONT_ONLY -> {
                if (secondaryFile != null && secondaryFile.exists()) {
                    writeFileToExternalStorageUseCase.write(secondaryFile, path)
                } else {
                    writeFileToExternalStorageUseCase.write(primaryFile, path)
                }
            }
            DualCameraSaveOption.BOTH_SEPARATELY -> {
                writeFileToExternalStorageUseCase.write(primaryFile, path)
            }
            DualCameraSaveOption.BOTH_TOGETHER -> {
                if (secondaryFile != null && secondaryFile.exists()) {
                    val mainCamera = dualCameraSettingsManager.primaryCamera.first()
                    val pipPosition = dualCameraSettingsManager.pipPosition.first()

                    val tempOut = File(context.cacheDir, "dual_combined_${videoId}_${System.currentTimeMillis()}.mp4")
                    val combined = dualVideoCombiner.combine(primaryFile, secondaryFile, tempOut, mainCamera, pipPosition)
                    if (combined && tempOut.exists() && tempOut.length() > 1000L) {
                        val result = writeFileToExternalStorageUseCase.write(tempOut, path)
                        tempOut.delete()
                        result
                    } else {
                        android.util.Log.e("ExportVideoContractImpl", "Failed to combine videos, fallback to primary")
                        writeFileToExternalStorageUseCase.write(primaryFile, path)
                    }
                } else {
                    writeFileToExternalStorageUseCase.write(primaryFile, path)
                }
            }
        }
    }

    override suspend fun exportSecondaryVideo(videoId: Long, path: Uri): Result<Unit> {
        val secondaryFile = videoRecordRepository.getSecondaryFileById(videoId)
            ?: return Result.failure(FileNotFoundException("Secondary video file not found"))
        if (!secondaryFile.exists()) {
            return Result.failure(FileNotFoundException("Secondary video file does not exist"))
        }
        return writeFileToExternalStorageUseCase.write(secondaryFile, path)
    }
}