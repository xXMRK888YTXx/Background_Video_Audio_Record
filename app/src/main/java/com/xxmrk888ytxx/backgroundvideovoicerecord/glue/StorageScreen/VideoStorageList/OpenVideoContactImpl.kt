package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.VideoStorageList

import android.content.Context
import androidx.core.net.toUri
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoRecordRepository.VideoRecordRepository
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.videoProcessing.DualVideoCombiner
import com.xxmrk888ytxx.coreandroid.Navigator
import com.xxmrk888ytxx.coreandroid.models.DualCameraSaveOption
import com.xxmrk888ytxx.storagescreen.VideoStorageList.contract.OpenVideoContract
import java.io.File
import javax.inject.Inject

import kotlinx.coroutines.flow.first

class OpenVideoContactImpl @Inject constructor(
    private val context: Context,
    private val videoRecordRepository: VideoRecordRepository,
    private val dualVideoCombiner: DualVideoCombiner,
    private val dualCameraSettingsManager: com.xxmrk888ytxx.backgroundvideovoicerecord.domain.dualCameraSettings.DualCameraSettingsManager
) : OpenVideoContract {

    override suspend fun openVideoContract(videoId: Long, navigator: Navigator) {
        val file = videoRecordRepository.getFileById(videoId) ?: return

        navigator.toVideoPlayerScreen(file.toUri())
    }

    override suspend fun openDualVideoContract(
        videoId: Long,
        option: DualCameraSaveOption,
        navigator: Navigator
    ) {
        val primaryFile = videoRecordRepository.getFileById(videoId) ?: return
        val secondaryFile = videoRecordRepository.getSecondaryFileById(videoId)

        when (option) {
            DualCameraSaveOption.BACK_ONLY -> {
                navigator.toVideoPlayerScreen(primaryFile.toUri())
            }
            DualCameraSaveOption.FRONT_ONLY -> {
                if (secondaryFile != null && secondaryFile.exists()) {
                    navigator.toVideoPlayerScreen(secondaryFile.toUri())
                } else {
                    navigator.toVideoPlayerScreen(primaryFile.toUri())
                }
            }
            DualCameraSaveOption.BOTH_SEPARATELY -> {
                navigator.toVideoPlayerScreen(primaryFile.toUri())
            }
            DualCameraSaveOption.BOTH_TOGETHER -> {
                if (secondaryFile != null && secondaryFile.exists()) {
                    val mainCamera = dualCameraSettingsManager.primaryCamera.first()
                    val pipPosition = dualCameraSettingsManager.pipPosition.first()

                    val cacheKey = "preview_combined_v12_${videoId}_${mainCamera.name}_${pipPosition.name}.mp4"
                    // Purge legacy preview caches to avoid stale videos
                    try {
                        context.cacheDir.listFiles()?.filter {
                            it.name.startsWith("preview_combined_") && !it.name.startsWith("preview_combined_v12_")
                        }?.forEach { it.delete() }
                    } catch (_: Exception) {}

                    val cacheCombined = File(context.cacheDir, cacheKey)
                    val isCachedValid = cacheCombined.exists() &&
                        cacheCombined.length() > 1000L &&
                        cacheCombined.lastModified() >= primaryFile.lastModified()

                    if (isCachedValid) {
                        navigator.toVideoPlayerScreen(cacheCombined.toUri())
                    } else {
                        val combined = dualVideoCombiner.combine(primaryFile, secondaryFile, cacheCombined, mainCamera, pipPosition)
                        if (combined && cacheCombined.exists() && cacheCombined.length() > 1000L) {
                            navigator.toVideoPlayerScreen(cacheCombined.toUri())
                        } else {
                            navigator.toVideoPlayerScreen(primaryFile.toUri())
                        }
                    }
                } else {
                    navigator.toVideoPlayerScreen(primaryFile.toUri())
                }
            }
        }
    }
}