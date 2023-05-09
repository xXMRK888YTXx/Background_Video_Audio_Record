package com.xxmrk888ytxx.settingsscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.settingsscreen.contracts.ManageCameraConfigContract
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.CameraMaxQualitySelectDialogState
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.CameraTypeSelectDialogState
import com.xxmrk888ytxx.settingsscreen.models.DialogState
import com.xxmrk888ytxx.settingsscreen.models.configs.CameraConfig
import com.xxmrk888ytxx.settingsscreen.models.configs.CameraMaxQuality
import com.xxmrk888ytxx.settingsscreen.models.configs.CameraType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val manageCameraConfigContract: ManageCameraConfigContract,
) : ViewModel() {

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

}