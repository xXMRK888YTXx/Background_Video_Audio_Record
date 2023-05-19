package com.xxmrk888ytxx.settingsscreen.models

import androidx.compose.runtime.Stable
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.CameraMaxQualitySelectDialogState
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.CameraRotationSelectDialogState
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.CameraTypeSelectDialogState
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.NotificationConfigurationDialogState

@Stable
data class DialogState(
    val cameraTypeSelectDialogState: CameraTypeSelectDialogState = CameraTypeSelectDialogState.Hidden,
    val cameraMaxQualitySelectDialogState: CameraMaxQualitySelectDialogState = CameraMaxQualitySelectDialogState.Hidden,
    val cameraRotationSelectDialogState: CameraRotationSelectDialogState = CameraRotationSelectDialogState.Hidden,
    val notificationConfigurationDialog: NotificationConfigurationDialogState = NotificationConfigurationDialogState.Hide
)
