package com.xxmrk888ytxx.settingsscreen

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.xxmrk888ytxx.coreandroid.Navigator
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
        ),
        SettingsParamType.Button(
            text = stringResource(R.string.dual_camera_primary),
            icon = R.drawable.camera_type,
            onClick = settingsViewModel::showDualCameraPrimarySelectDialogState
        ),
        SettingsParamType.Button(
            text = stringResource(R.string.dual_camera_pip_position),
            icon = R.drawable.rotation,
            onClick = settingsViewModel::showPipPositionSelectDialogState
        ),
        SettingsParamType.Button(
            text = "Авто-нарезка видео (лимит 4 ГБ / 30 мин)",
            icon = R.drawable.camera_type,
            onClick = settingsViewModel::showVideoSplitLimitSelectDialog
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

@SuppressLint("ResourceType")
@Composable
internal fun aboutApplicationParams(
    settingsViewModel: SettingsViewModel
) : ImmutableList<SettingsParamType> {

    return persistentListOf(
        SettingsParamType.Label(
            text = stringResource(R.string.Application_version),
            icon = R.drawable.info,
            secondaryText = settingsViewModel.appVersion
        ),

        SettingsParamType.Button(
            text = stringResource(R.string.Source_code),
            icon = R.drawable.code,
            onClick = settingsViewModel::openSourceCode
        ),

        SettingsParamType.Button(
            text = stringResource(R.string.Write_developer),
            icon = R.drawable.email,
            onClick = settingsViewModel::openEmailSenderApp
        ),

        SettingsParamType.Button(
            text = stringResource(R.string.Privacy_policy),
            icon = R.drawable.shield,
            onClick = settingsViewModel::openPrivacyPolicy
        ),

        SettingsParamType.Button(
            text = stringResource(R.string.Terms_of_use),
            icon = R.drawable.terms,
            onClick = settingsViewModel::openTermsOfUse
        ),
    )
}

@SuppressLint("ResourceType")
@Composable
internal fun autoExportToExternalStorageParams(
    settingsViewModel: SettingsViewModel,
    navigator: Navigator
) : ImmutableList<SettingsParamType> {

    return persistentListOf(
        SettingsParamType.Button(
            text = stringResource(R.string.automatic_copying_to_external_memory),
            icon = R.drawable.baseline_sd_storage_24,
            onClick = { settingsViewModel.openAutoExportToExternalStorageScreen(navigator) }
        ),
    )
}

@SuppressLint("ResourceType")
@Composable
internal fun securityParams(
    settingsViewModel: SettingsViewModel
): ImmutableList<SettingsParamType> {
    val isAppLockEnabled by settingsViewModel.isAppLockEnabled.collectAsState(initial = false)
    val isBiometricEnabled by settingsViewModel.isBiometricEnabled.collectAsState(initial = false)

    val list = persistentListOf<SettingsParamType>().builder()

    list.add(
        SettingsParamType.Switch(
            text = "Блокировка приложения (PIN-код)",
            icon = R.drawable.ic_lock,
            isSwitched = isAppLockEnabled,
            onStateChanged = settingsViewModel::onAppLockSwitchChanged
        )
    )

    if (isAppLockEnabled) {
        list.add(
            SettingsParamType.Button(
                text = "Изменить PIN-код",
                icon = R.drawable.ic_lock,
                onClick = { settingsViewModel.showSetPinDialog(isChangeMode = true) }
            )
        )

        if (settingsViewModel.isBiometricsSupported) {
            list.add(
                SettingsParamType.Switch(
                    text = "Разблокировка по отпечатку пальца",
                    icon = R.drawable.ic_fingerprint,
                    isSwitched = isBiometricEnabled,
                    onStateChanged = settingsViewModel::onBiometricSwitchChanged
                )
            )
        }
    }

    return list.build()
}

@SuppressLint("ResourceType")
@Composable
internal fun hardwareTriggerParams(
    settingsViewModel: SettingsViewModel
): ImmutableList<SettingsParamType> {
    val isVolumeTriggerEnabled by settingsViewModel.isVolumeTriggerEnabled.collectAsState(initial = false)
    val isProximityTriggerEnabled by settingsViewModel.isProximityTriggerEnabled.collectAsState(initial = false)
    val isHapticFeedbackEnabled by settingsViewModel.isHapticFeedbackEnabled.collectAsState(initial = true)

    val list = persistentListOf<SettingsParamType>().builder()

    list.add(
        SettingsParamType.Switch(
            text = "Запуск кнопками громкости (3x Вниз)",
            icon = R.drawable.baseline_edit_notifications_24,
            isSwitched = isVolumeTriggerEnabled,
            onStateChanged = settingsViewModel::onVolumeTriggerSwitchChanged
        )
    )

    if (isVolumeTriggerEnabled && !settingsViewModel.isAccessibilityServiceEnabled) {
        list.add(
            SettingsParamType.Button(
                text = "Включить службу в спец. возможностях",
                icon = R.drawable.arrow,
                onClick = settingsViewModel::openAccessibilitySettings
            )
        )
    }

    list.add(
        SettingsParamType.Switch(
            text = "Запуск по датчику приближения (2 взмаха)",
            icon = R.drawable.camera_type,
            isSwitched = isProximityTriggerEnabled,
            onStateChanged = settingsViewModel::onProximityTriggerSwitchChanged
        )
    )

    list.add(
        SettingsParamType.Switch(
            text = "Виброотклик при старте и остановке",
            icon = R.drawable.rotation,
            isSwitched = isHapticFeedbackEnabled,
            onStateChanged = settingsViewModel::onHapticFeedbackSwitchChanged
        )
    )

    return list.build()
}