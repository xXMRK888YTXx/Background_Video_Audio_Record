package com.xxmrk888ytxx.settingsscreen

import android.graphics.Camera
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.settingsscreen.contracts.ManageCameraConfigContract
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.CameraTypeSelectDialogState
import com.xxmrk888ytxx.settingsscreen.models.DialogState
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
    internal val cameraType = manageCameraConfigContract.currentCameraType
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CameraType.Back)

    internal fun changeCurrentCameraType(newCameraType: CameraType) {
        viewModelScope.launch(Dispatchers.IO) {
            manageCameraConfigContract.setupCameraType(newCameraType)
        }
    }
    //

    //Dialog state
    private val _dialogState = MutableStateFlow(DialogState())

    internal val dialogState = _dialogState.asStateFlow()
    //

    //CameraTypeSelectDialog
    internal fun showCameraTypeSelectDialog() {
        viewModelScope.launch {
            _dialogState.update {
                it.copy(
                    isCameraTypeSelectDialogVisible = CameraTypeSelectDialogState.Showed(
                        cameraType.first()
                    )
                )
            }
        }
    }

    internal fun hideCameraTypeSelectDialog() {
        _dialogState.update { it.copy(isCameraTypeSelectDialogVisible = CameraTypeSelectDialogState.Hidden) }
    }

    //

}