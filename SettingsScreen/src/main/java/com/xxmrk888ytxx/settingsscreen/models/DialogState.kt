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
    val notificationConfigurationDialog: NotificationConfigurationDialogState = NotificationConfigurationDialogState.Hide
)
