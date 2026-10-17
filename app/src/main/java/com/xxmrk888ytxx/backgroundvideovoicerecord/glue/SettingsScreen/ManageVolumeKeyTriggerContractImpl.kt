package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.SettingsScreen

import android.content.Context
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.volumeKey.VolumeKeyActionManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.volumeKey.VolumeKeyTriggerService
import com.xxmrk888ytxx.coreandroid.models.QuickTileCameraType
import com.xxmrk888ytxx.coreandroid.models.VolumeKeyAction
import com.xxmrk888ytxx.settingsscreen.contracts.ManageVolumeKeyTriggerContract
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageVolumeKeyTriggerContractImpl @Inject constructor(
    private val context: Context,
    private val volumeKeyActionManager: VolumeKeyActionManager
) : ManageVolumeKeyTriggerContract {

    override val isTriggerEnabled: Flow<Boolean> = volumeKeyActionManager.isTriggerEnabled

    override val isVolumeKeyPermissionGranted: Boolean
        get() = volumeKeyActionManager.isVolumeKeyPermissionGranted()

    override val isOverlayPermissionGranted: Boolean
        get() = volumeKeyActionManager.isOverlayPermissionGranted()

    override val isAllPermissionsGranted: Boolean
        get() = volumeKeyActionManager.isAllPermissionsGranted()

    override val volumeKeyAction: Flow<VolumeKeyAction> = volumeKeyActionManager.currentAction

    override val volumeKeyCameraType: Flow<QuickTileCameraType> = volumeKeyActionManager.currentCameraType

    override suspend fun setTriggerEnabled(enabled: Boolean) {
        volumeKeyActionManager.setTriggerEnabled(enabled)
        if (enabled && isAllPermissionsGranted) {
            VolumeKeyTriggerService.start(context)
            VolumeKeyTriggerService.scheduleWatchdog(context)
        } else {
            VolumeKeyTriggerService.stop(context)
        }
    }

    override suspend fun setVolumeKeyAction(action: VolumeKeyAction) {
        volumeKeyActionManager.setAction(action)
    }

    override suspend fun setVolumeKeyCameraType(cameraType: QuickTileCameraType) {
        volumeKeyActionManager.setCameraType(cameraType)
    }
}
