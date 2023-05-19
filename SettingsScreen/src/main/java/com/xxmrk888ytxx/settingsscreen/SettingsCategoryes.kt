package com.xxmrk888ytxx.settingsscreen

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.NotificationConfigurationDialogState
import com.xxmrk888ytxx.settingsscreen.models.SettingsParamType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@SuppressLint("ResourceType")
@Composable
internal fun cameraParams(settingsViewModel: SettingsViewModel): ImmutableList<SettingsParamType> {

    return persistentListOf(
        SettingsParamType.Button(
            stringResource(R.string.Camera_type),
            icon = R.drawable.camera_type,
            onClick = settingsViewModel::showCameraTypeSelectDialog
        ),
        SettingsParamType.Button(
            stringResource(R.string.Record_quality),
            icon = R.drawable.camera_type,
            onClick = settingsViewModel::showCameraMaxQualitySelectDialogState
        ),
        SettingsParamType.Button(
            text = stringResource(R.string.Camera_rotation),
            icon = R.drawable.rotation,
            onClick = settingsViewModel::showCameraRotationSelectDialogState
        )
    )
}

@SuppressLint("ResourceType")
@Composable
internal fun notificationParams(settingsViewModel: SettingsViewModel): ImmutableList<SettingsParamType> {

    return persistentListOf(
        SettingsParamType.Button(
            text = stringResource(R.string.Notification_audio_params),
            icon = R.drawable.baseline_edit_notifications_24,
            onClick = remember {
                {
                    settingsViewModel.showNotificationConfigurationDialog(
                        NotificationConfigurationDialogState.ConfigurationForState.AUDIO_NOTIFICATION
                    )
                }
            }
        ),
        SettingsParamType.Button(
            text = stringResource(R.string.Notification_video_params),
            icon = R.drawable.baseline_edit_notifications_24,
            onClick = remember {
                {
                    settingsViewModel.showNotificationConfigurationDialog(
                        NotificationConfigurationDialogState.ConfigurationForState.VIDEO_NOTIFICATION
                    )
                }
            }
        )
    )
}