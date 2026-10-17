package com.xxmrk888ytxx.settingsscreen.models

import androidx.compose.runtime.Stable
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.CameraMaxQualitySelectDialogState
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.CameraRotationSelectDialogState
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.CameraTypeSelectDialogState
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.NotificationConfigurationDialogState

import com.xxmrk888ytxx.settingsscreen.models.DialogModels.DualCameraPrimarySelectDialogState
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.PipPositionSelectDialogState

@Stable
data class DialogState(
    val cameraTypeSelectDialogState: CameraTypeSelectDialogState = CameraTypeSelectDialogState.Hidden,
    val cameraMaxQualitySelectDialogState: CameraMaxQualitySelectDialogState = CameraMaxQualitySelectDialogState.Hidden,
    val cameraRotationSelectDialogState: CameraRotationSelectDialogState = CameraRotationSelectDialogState.Hidden,
    val dualCameraPrimarySelectDialogState: DualCameraPrimarySelectDialogState = DualCameraPrimarySelectDialogState.Hidden,
    val pipPositionSelectDialogState: PipPositionSelectDialogState = PipPositionSelectDialogState.Hidden,
    val videoSplitLimitSelectDialogState: com.xxmrk888ytxx.settingsscreen.models.DialogModels.VideoSplitLimitSelectDialogState = com.xxmrk888ytxx.settingsscreen.models.DialogModels.VideoSplitLimitSelectDialogState.Hidden,
    val notificationConfigurationDialog: NotificationConfigurationDialogState = NotificationConfigurationDialogState.Hide,
    val setPinDialogState: com.xxmrk888ytxx.settingsscreen.models.DialogModels.SetPinDialogState = com.xxmrk888ytxx.settingsscreen.models.DialogModels.SetPinDialogState.Hidden,
    val quickTileActionSelectDialogState: com.xxmrk888ytxx.settingsscreen.models.DialogModels.QuickTileActionSelectDialogState = com.xxmrk888ytxx.settingsscreen.models.DialogModels.QuickTileActionSelectDialogState.Hidden,
    val quickTileCameraSelectDialogState: com.xxmrk888ytxx.settingsscreen.models.DialogModels.QuickTileCameraSelectDialogState = com.xxmrk888ytxx.settingsscreen.models.DialogModels.QuickTileCameraSelectDialogState.Hidden,
    val volumeKeyActionSelectDialogState: com.xxmrk888ytxx.settingsscreen.models.DialogModels.VolumeKeyActionSelectDialogState = com.xxmrk888ytxx.settingsscreen.models.DialogModels.VolumeKeyActionSelectDialogState.Hidden,
    val volumeKeyCameraSelectDialogState: com.xxmrk888ytxx.settingsscreen.models.DialogModels.VolumeKeyCameraSelectDialogState = com.xxmrk888ytxx.settingsscreen.models.DialogModels.VolumeKeyCameraSelectDialogState.Hidden,
    val adbPermissionDialogState: com.xxmrk888ytxx.settingsscreen.models.DialogModels.AdbPermissionDialogState = com.xxmrk888ytxx.settingsscreen.models.DialogModels.AdbPermissionDialogState.Hidden
)
