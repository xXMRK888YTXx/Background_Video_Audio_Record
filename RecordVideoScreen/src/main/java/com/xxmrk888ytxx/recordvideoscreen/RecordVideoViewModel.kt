package com.xxmrk888ytxx.recordvideoscreen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.material.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.coreandroid.ToastManager
import com.xxmrk888ytxx.corecompose.Shared.RequestPermissionDialog.RequestedPermissionModel
import com.xxmrk888ytxx.recordvideoscreen.contract.ManageCameraTypeContract
import com.xxmrk888ytxx.recordvideoscreen.contract.RecordVideoManageContract
import com.xxmrk888ytxx.recordvideoscreen.contract.RecordVideoStateProviderContract
import com.xxmrk888ytxx.recordvideoscreen.exceptions.OtherRecordServiceStartedException
import com.xxmrk888ytxx.recordvideoscreen.models.CurrentSelectedCameraModel
import com.xxmrk888ytxx.recordvideoscreen.models.DialogState
import com.xxmrk888ytxx.recordvideoscreen.models.RecordState
import com.xxmrk888ytxx.recordvideoscreen.models.RecordWidgetColor
import com.xxmrk888ytxx.recordvideoscreen.models.ViewType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class RecordVideoViewModel @Inject constructor(
    private val recordVideoManageContract: RecordVideoManageContract,
    private val recordVideoStateProviderContract: RecordVideoStateProviderContract,
    private val context: Context,
    private val manageCameraTypeContract: ManageCameraTypeContract,
    private val toastManager: ToastManager
) : ViewModel() {

    //Show toast
    fun showToast(text:Int) {
        toastManager.showToast(text)
    }
    //

    //Manage Camera Type

    internal val currentCamera = manageCameraTypeContract.cameraType
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            CurrentSelectedCameraModel.Back
        )

    internal fun changeCurrentSelectedCamera(currentCamera: CurrentSelectedCameraModel) {
        viewModelScope.launch(Dispatchers.IO) {
            if (currentCamera is CurrentSelectedCameraModel.Back) {
                manageCameraTypeContract.setCurrentSelectedCamera(CurrentSelectedCameraModel.Front)
            } else {
                manageCameraTypeContract.setCurrentSelectedCamera(CurrentSelectedCameraModel.Back)
            }
        }
    }
    //

    //Snackbar
    private var snackBarHostState: SnackbarHostState? = null

    internal fun initSnackBarHostState(snackBarHostState: SnackbarHostState) {
        this.snackBarHostState = snackBarHostState
    }
    //

    //View type
    private val _viewType: MutableStateFlow<ViewType> = MutableStateFlow(ViewType.RecordWidget)

    internal val viewType = _viewType.asStateFlow()

    fun toRecordWidgetViewType() {
        _viewType.update { ViewType.RecordWidget }
    }

    fun toCameraPreviewType() {
        if (!isCameraPermissionGranted) {
            requestCameraPermission()

            return
        }
        _viewType.update { ViewType.CameraPreview }
    }
    //

    //Widget color
    private val _currentWidgetColor = MutableStateFlow(RecordWidgetColor())

    internal val currentWidgetColor = _currentWidgetColor.asStateFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000), RecordWidgetColor()
        )

    fun regenerateButtonGradientColor() {
        _currentWidgetColor.update { it.newRecordGradient }
    }
    //

    //DialogState
    private val _dialogState = MutableStateFlow(DialogState())

    internal val dialogState = _dialogState.asStateFlow()
    //

    //Permission dialog

    fun showPermissionDialog() {
        _dialogState.update { it.copy(isPermissionStateVisible = true) }
    }

    fun hidePermissionDialog() {
        _dialogState.update { it.copy(isPermissionStateVisible = false) }
    }
    //

    //Permissions state

    private val isAllPermissionGranted: Boolean
        get() = isRecordAudioPermissionGranted
                && isCameraPermissionGranted
                && isPostNotificationPermissionGranted

    private val isRecordAudioPermissionGranted: Boolean
        get() = context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

    private val isCameraPermissionGranted: Boolean
        get() = context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    private val isPostNotificationPermissionGranted: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

    @SuppressLint("ResourceType")
    private val _recordAudioPermissionState: MutableStateFlow<RequestedPermissionModel> =
        MutableStateFlow(
            RequestedPermissionModel(
                description = R.string.Audio_record_permission_description,
                isGranted = isRecordAudioPermissionGranted,
                onRequest = ::requestAudioPermission
            )
        )

    @SuppressLint("ResourceType")
    private val _cameraPermissionState: MutableStateFlow<RequestedPermissionModel> =
        MutableStateFlow(
            RequestedPermissionModel(
                description = R.string.Audio_record_permission_description,
                isGranted = isCameraPermissionGranted,
                onRequest = ::requestCameraPermission
            )
        )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("ResourceType")
    private val _postNotificationPermissionState: MutableStateFlow<RequestedPermissionModel> =
        MutableStateFlow(
            RequestedPermissionModel(
                description = R.string.Post_notification_description,
                isGranted = isPostNotificationPermissionGranted,
                onRequest = ::requestPostNotificationPermission
            )
        )

    internal val recordAudioPermissionState = _recordAudioPermissionState.asStateFlow()

    internal val cameraPermissionState = _cameraPermissionState.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    internal val postNotificationPermissionState = _postNotificationPermissionState.asStateFlow()
    //

    //Permission request
    private fun requestAudioPermission() {
        requestAudioRecordPermissionContract?.launch(Manifest.permission.RECORD_AUDIO)
    }

    private fun requestCameraPermission() {
        requestCameraPermissionContract?.launch(Manifest.permission.CAMERA)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPostNotificationPermission() {
        requestPostNotificationPermissionContract?.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
    //

    //Permission handle result
    fun onRecordAudioPermissionResult(isGranted: Boolean) {
        _recordAudioPermissionState.update { it.copy(isGranted = isGranted) }
    }

    fun onCameraPermissionResult(isGranted: Boolean) {
        _cameraPermissionState.update { it.copy(isGranted = isGranted) }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun onPostNotificationPermissionResult(isGranted: Boolean) {
        _postNotificationPermissionState.update { it.copy(isGranted = isGranted) }
    }
    //

    //Record
    internal val currentRecordState = recordVideoStateProviderContract.currentState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RecordState.Idle)

    fun startRecord() {
        if (!isAllPermissionGranted) {
            showPermissionDialog()

            return
        }

        viewModelScope.launch {
            try {
                recordVideoManageContract.start()
            } catch (e: OtherRecordServiceStartedException) {
                snackBarHostState?.showSnackbar(context.getString(R.string.Audio_recording_is_started))
            }
        }
    }

    fun stopRecord() {
        viewModelScope.launch { recordVideoManageContract.stop() }
    }

    fun pauseRecord() {
        viewModelScope.launch { recordVideoManageContract.pause() }
    }

    fun resumeRecord() {
        viewModelScope.launch { recordVideoManageContract.resume() }
    }
    //

    //Activity result contracts
    private var requestAudioRecordPermissionContract: ActivityResultLauncher<String>? = null

    private var requestCameraPermissionContract: ActivityResultLauncher<String>? = null

    private var requestPostNotificationPermissionContract: ActivityResultLauncher<String>? = null

    fun initActivityResultContracts(
        requestAudioRecordPermissionContract: ActivityResultLauncher<String>,
        requestCameraPermissionContract: ActivityResultLauncher<String>,
        requestPostNotificationPermissionContract: ActivityResultLauncher<String>?,
    ) {
        this.requestAudioRecordPermissionContract = requestAudioRecordPermissionContract
        this.requestCameraPermissionContract = requestCameraPermissionContract
        this.requestPostNotificationPermissionContract = requestPostNotificationPermissionContract
    }
    //

}