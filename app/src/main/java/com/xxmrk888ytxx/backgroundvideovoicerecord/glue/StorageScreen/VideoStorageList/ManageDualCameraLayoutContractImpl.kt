package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.VideoStorageList

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.dualCameraSettings.DualCameraSettingsManager
import com.xxmrk888ytxx.coreandroid.models.DualCameraPrimary
import com.xxmrk888ytxx.coreandroid.models.PipPosition
import com.xxmrk888ytxx.storagescreen.VideoStorageList.contract.ManageDualCameraLayoutContract
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageDualCameraLayoutContractImpl @Inject constructor(
    private val dualCameraSettingsManager: DualCameraSettingsManager
) : ManageDualCameraLayoutContract {
    override val primaryCamera: Flow<DualCameraPrimary> = dualCameraSettingsManager.primaryCamera
    override val pipPosition: Flow<PipPosition> = dualCameraSettingsManager.pipPosition

    override suspend fun setPrimaryCamera(primary: DualCameraPrimary) {
        dualCameraSettingsManager.setPrimaryCamera(primary)
    }

    override suspend fun setPipPosition(position: PipPosition) {
        dualCameraSettingsManager.setPipPosition(position)
    }
}
