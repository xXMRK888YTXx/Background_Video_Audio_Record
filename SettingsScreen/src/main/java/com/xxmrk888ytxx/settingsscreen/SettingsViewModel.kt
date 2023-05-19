package com.xxmrk888ytxx.settingsscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.settingsscreen.contracts.ManageAudioNotificationConfigContract
import com.xxmrk888ytxx.settingsscreen.contracts.ManageCameraConfigContract
import com.xxmrk888ytxx.settingsscreen.contracts.ManageVideoNotificationConfigContract
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.CameraMaxQualitySelectDialogState
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.CameraRotationSelectDialogState
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.CameraTypeSelectDialogState
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.NotificationConfigurationDialogState
import com.xxmrk888ytxx.settingsscreen.models.DialogState
import com.xxmrk888ytxx.settingsscreen.models.NotificationConfigType
import com.xxmrk888ytxx.settingsscreen.models.configs.CameraMaxQuality
import com.xxmrk888ytxx.settingsscreen.models.configs.CameraRotation
import com.xxmrk888ytxx.settingsscreen.models.configs.CameraType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val manageCameraConfigContract: ManageCameraConfigContract,
    private val manageAudioNotificationConfigContract: ManageAudioNotificationConfigContract,
    private val manageVideoNotificationConfigContract: ManageVideoNotificationConfigContract
) : ViewModel() {

    //NotificationConfigurationDialog
    internal fun showNotificationConfigurationDialog(
        configurationForState: NotificationConfigurationDialogState.ConfigurationForState
    ) {

        viewModelScope.launch {
            val initialState = when(configurationForState) {
                NotificationConfigurationDialogState.ConfigurationForState.AUDIO_NOTIFICATION -> {
                    manageAudioNotificationConfigContract.currentConfig.first()
                }
                NotificationConfigurationDialogState.ConfigurationForState.VIDEO_NOTIFICATION -> {
                    manageVideoNotificationConfigContract.currentConfig.first()
                }
            }

            _dialogState.update {
                it.copy(
                    notificationConfigurationDialog = NotificationConfigurationDialogState.Showed(
                        initialState = initialState,
                        configurationForState = configurationForState
                    )
                )
            }
        }
    }

    internal fun hideNotificationConfigurationDialog() {
        _dialogState.update {
            it.copy(
                notificationConfigurationDialog = NotificationConfigurationDialogState.Hide
            )
        }
    }

    internal fun setNotificationConfig(newConfig: NotificationConfigType) {
        val dialogState  = _dialogState.value.notificationConfigurationDialog as?
                NotificationConfigurationDialogState.Showed ?: return

        viewModelScope.launch(Dispatchers.IO) {
            when(dialogState.configurationForState) {

                NotificationConfigurationDialogState.ConfigurationForState.AUDIO_NOTIFICATION -> {
                    manageAudioNotificationConfigContract.setConfig(newConfig)
                }

                NotificationConfigurationDialogState.ConfigurationForState.VIDEO_NOTIFICATION -> {
                    manageVideoNotificationConfigContract.setConfig(newConfig)
                }
            }

            hideNotificationConfigurationDialog()
        }
    }
    //

    //Camera config
    private val cameraConfig = manageCameraConfigContract.cameraConfig

    internal fun changeCurrentCameraType(newCameraType: CameraType) {
        viewModelScope.launch(Dispatchers.IO) {
            manageCameraConfigContract.setupCameraType(newCameraType)
        }
    }

    internal fun changeCurrentCameraMaxQuality(maxQuality: CameraMaxQuality) {
        viewModelScope.launch(Dispatchers.IO) {
            manageCameraConfigContract.setupCameraMaxQuality(maxQuality)
        }
    }

    internal fun changeCameraRotation(cameraRotation: CameraRotation) {
        viewModelScope.launch(Dispatchers.IO) {
            manageCameraConfigContract.setupCameraRotation(cameraRotation)
        }
    }
    //

    //Dialog state
    private val _dialogState = MutableStateFlow(DialogState())

    internal val dialogState = _dialogState.asStateFlow()
    //

    //CameraMaxQualitySelectDialog
    internal fun showCameraMaxQualitySelectDialogState() {
        viewModelScope.launch {
            _dialogState.update {
                it.copy(
                    cameraMaxQualitySelectDialogState = CameraMaxQualitySelectDialogState.Showed(
                        cameraConfig.first().cameraMaxQuality
                    )
                )
            }
        }
    }

    internal fun hideCameraMaxQualitySelectDialogState() {
        _dialogState.update {
            it.copy(
                cameraMaxQualitySelectDialogState = CameraMaxQualitySelectDialogState.Hidden
            )
        }
    }
    //

    //CameraTypeSelectDialog
    internal fun showCameraTypeSelectDialog() {
        viewModelScope.launch {
            _dialogState.update {
                it.copy(
                    cameraTypeSelectDialogState = CameraTypeSelectDialogState.Showed(
                        cameraConfig.first().cameraType
                    )
                )
            }
        }
    }

    internal fun hideCameraTypeSelectDialog() {
        _dialogState.update { it.copy(cameraTypeSelectDialogState = CameraTypeSelectDialogState.Hidden) }
    }

    //

    //CameraRotationSelectDialogState
    internal fun showCameraRotationSelectDialogState() {
        viewModelScope.launch {
            _dialogState.update {
                it.copy(
                    cameraRotationSelectDialogState = CameraRotationSelectDialogState.Showed(
                        cameraConfig.first().cameraRotation
                    )
                )
            }
        }
    }

    internal fun hideCameraRotationSelectDialogState() {
        viewModelScope.launch {
            _dialogState.update {
                it.copy(
                    cameraRotationSelectDialogState = CameraRotationSelectDialogState.Hidden
                )
            }
        }
    }
    //

}