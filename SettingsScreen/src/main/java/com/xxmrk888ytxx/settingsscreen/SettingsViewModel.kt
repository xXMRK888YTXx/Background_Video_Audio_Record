package com.xxmrk888ytxx.settingsscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.coreandroid.Navigator
import com.xxmrk888ytxx.settingsscreen.contracts.ManageAudioNotificationConfigContract
import com.xxmrk888ytxx.settingsscreen.contracts.ManageCameraConfigContract
import com.xxmrk888ytxx.settingsscreen.contracts.ManageVideoNotificationConfigContract
import com.xxmrk888ytxx.settingsscreen.contracts.OpenActivityContract
import com.xxmrk888ytxx.settingsscreen.contracts.ProvideAppVersionContract
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.CameraMaxQualitySelectDialogState
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.CameraRotationSelectDialogState
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.CameraTypeSelectDialogState
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.NotificationConfigurationDialogState
import com.xxmrk888ytxx.settingsscreen.models.DialogState
import com.xxmrk888ytxx.coreandroid.models.DualCameraPrimary
import com.xxmrk888ytxx.coreandroid.models.PipPosition
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.DualCameraPrimarySelectDialogState
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.PipPositionSelectDialogState
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
import com.xxmrk888ytxx.settingsscreen.contracts.ManageAppLockContract
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val manageCameraConfigContract: ManageCameraConfigContract,
    private val manageAudioNotificationConfigContract: ManageAudioNotificationConfigContract,
    private val manageVideoNotificationConfigContract: ManageVideoNotificationConfigContract,
    private val provideAppVersionContract: ProvideAppVersionContract,
    private val openActivityContract: OpenActivityContract,
    private val manageAppLockContract: ManageAppLockContract
) : ViewModel() {

    //Open terms of use
    internal fun openTermsOfUse() {
        openActivityContract.openTermsOfUse()
    }
    //

    //Open privacy policy
    internal fun openPrivacyPolicy() {
        openActivityContract.openPrivacyPolicy()
    }
    //


    //Open email sender app
    internal fun openEmailSenderApp() {
        openActivityContract.openEmailSender()
    }
    //


    //Open source code
    internal fun openSourceCode() {
        openActivityContract.openSourceCode()
    }
    //

    internal fun openAutoExportToExternalStorageScreen(navigator: Navigator) {
        navigator.toAutoExportToExternalStorageScreen()
    }


    //App version
    internal val appVersion : String
        get() = provideAppVersionContract.appVersion
    //

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

    //DualCameraPrimarySelectDialog
    internal fun showDualCameraPrimarySelectDialogState() {
        viewModelScope.launch {
            _dialogState.update {
                it.copy(
                    dualCameraPrimarySelectDialogState = DualCameraPrimarySelectDialogState.Showed(
                        manageCameraConfigContract.dualCameraPrimary.first()
                    )
                )
            }
        }
    }

    internal fun hideDualCameraPrimarySelectDialogState() {
        _dialogState.update {
            it.copy(dualCameraPrimarySelectDialogState = DualCameraPrimarySelectDialogState.Hidden)
        }
    }

    internal fun changeDualCameraPrimary(primary: DualCameraPrimary) {
        viewModelScope.launch(Dispatchers.IO) {
            manageCameraConfigContract.setupDualCameraPrimary(primary)
        }
    }
    //

    //PipPositionSelectDialog
    internal fun showPipPositionSelectDialogState() {
        viewModelScope.launch {
            _dialogState.update {
                it.copy(
                    pipPositionSelectDialogState = PipPositionSelectDialogState.Showed(
                        manageCameraConfigContract.pipPosition.first()
                    )
                )
            }
        }
    }

    internal fun hidePipPositionSelectDialogState() {
        _dialogState.update {
            it.copy(pipPositionSelectDialogState = PipPositionSelectDialogState.Hidden)
        }
    }

    internal fun changePipPosition(position: PipPosition) {
        viewModelScope.launch(Dispatchers.IO) {
            manageCameraConfigContract.setupPipPosition(position)
        }
    }
    //

    //VideoSplitLimitSelectDialog
    internal val videoSplitLimit: kotlinx.coroutines.flow.Flow<com.xxmrk888ytxx.coreandroid.models.VideoSplitLimit> =
        manageCameraConfigContract.videoSplitLimit

    internal fun showVideoSplitLimitSelectDialog() {
        viewModelScope.launch {
            _dialogState.update {
                it.copy(
                    videoSplitLimitSelectDialogState = com.xxmrk888ytxx.settingsscreen.models.DialogModels.VideoSplitLimitSelectDialogState.Showed(
                        manageCameraConfigContract.videoSplitLimit.first()
                    )
                )
            }
        }
    }

    internal fun hideVideoSplitLimitSelectDialog() {
        _dialogState.update {
            it.copy(videoSplitLimitSelectDialogState = com.xxmrk888ytxx.settingsscreen.models.DialogModels.VideoSplitLimitSelectDialogState.Hidden)
        }
    }

    internal fun changeVideoSplitLimit(limit: com.xxmrk888ytxx.coreandroid.models.VideoSplitLimit) {
        viewModelScope.launch(Dispatchers.IO) {
            manageCameraConfigContract.setupVideoSplitLimit(limit)
        }
    }
    //

    // App Lock
    internal val isAppLockEnabled: kotlinx.coroutines.flow.Flow<Boolean> =
        manageAppLockContract.isAppLockEnabled

    internal val isBiometricEnabled: kotlinx.coroutines.flow.Flow<Boolean> =
        manageAppLockContract.isBiometricEnabled

    internal val isBiometricsSupported: Boolean
        get() = manageAppLockContract.isBiometricsSupported

    internal fun onAppLockSwitchChanged(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled) {
                val isPinSet = manageAppLockContract.isPinCodeSet.first()
                if (!isPinSet) {
                    showSetPinDialog(isChangeMode = false)
                } else {
                    manageAppLockContract.setAppLockEnabled(true)
                }
            } else {
                manageAppLockContract.setAppLockEnabled(false)
            }
        }
    }

    internal fun showSetPinDialog(isChangeMode: Boolean = true) {
        _dialogState.update {
            it.copy(setPinDialogState = com.xxmrk888ytxx.settingsscreen.models.DialogModels.SetPinDialogState.Showed(isChangeMode))
        }
    }

    internal fun hideSetPinDialog() {
        _dialogState.update {
            it.copy(setPinDialogState = com.xxmrk888ytxx.settingsscreen.models.DialogModels.SetPinDialogState.Hidden)
        }
    }

    internal fun onPinCodeConfirmed(pin: String) {
        viewModelScope.launch(Dispatchers.IO) {
            manageAppLockContract.setPinCode(pin)
            manageAppLockContract.setAppLockEnabled(true)
            hideSetPinDialog()
        }
    }

    internal fun onBiometricSwitchChanged(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            manageAppLockContract.setBiometricEnabled(enabled)
        }
    }
    //
}