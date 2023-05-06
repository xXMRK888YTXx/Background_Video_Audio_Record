package com.xxmrk888ytxx.recordaudioscreen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.material.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.coreandroid.ToastManager
import com.xxmrk888ytxx.corecompose.Shared.RequestPermissionDialog.RequestedPermissionModel
import com.xxmrk888ytxx.recordaudioscreen.contracts.RecordManageContract
import com.xxmrk888ytxx.recordaudioscreen.contracts.RecordStateProviderContract
import com.xxmrk888ytxx.recordaudioscreen.exceptions.OtherRecordStartedException
import com.xxmrk888ytxx.recordaudioscreen.models.DialogState
import com.xxmrk888ytxx.recordaudioscreen.models.RecordState
import com.xxmrk888ytxx.recordaudioscreen.models.RecordWidgetColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.reflect.KClass

class RecordAudioViewModel @Inject constructor(
    private val recordManageContract: RecordManageContract,
    private val recordStateProviderContract: RecordStateProviderContract,
    private val context: Context,
    private val toastManager: ToastManager
) : ViewModel() {

    //DelayStartRecordConfigurationDialog
    fun showDelayStartRecordConfigurationDialog() {
        if(!isAllPermissionGranted) {
            _dialogState.update { it.copy(isPermissionDialogVisible = true) }

            return
        }

        _dialogState.update { it.copy(isDelayStartRecordConfigurationDialogVisible = true) }
    }

    fun hideDelayStartRecordConfigurationDialog() {
        _dialogState.update { it.copy(isDelayStartRecordConfigurationDialogVisible = false) }
    }
    //

    //Snackbar
    private var snackBarHostState: SnackbarHostState? = null

    internal fun initSnackBarHostState(snackBarHostState: SnackbarHostState) {
        this.snackBarHostState = snackBarHostState
    }
    //

    private var requestAudioRecordPermissionContract: ActivityResultLauncher<String>? = null

    private var requestPostNotificationPermissionContract: ActivityResultLauncher<String>? = null

    private val _currentWidgetColor = MutableStateFlow(RecordWidgetColor())

    internal val currentWidgetColor = _currentWidgetColor.asStateFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000), RecordWidgetColor()
        )

    internal val recordState: Flow<RecordState> = recordStateProviderContract.currentState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RecordState.Idle)


    private val _dialogState: MutableStateFlow<DialogState> = MutableStateFlow(DialogState())

    internal val dialogState = _dialogState.asStateFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DialogState())

    private val isAudioRecordPermissionGranted: Boolean
        get() = context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

    private val isPostNotificationPermissionGranted: Boolean
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        get() = context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    private val isAllPermissionGranted: Boolean
        get() = isAudioRecordPermissionGranted && if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            isPostNotificationPermissionGranted
        else true

    @SuppressLint("ResourceType")
    private val _recordAudioPermissionState: MutableStateFlow<RequestedPermissionModel> =
        MutableStateFlow(
            RequestedPermissionModel(
                description = R.string.Record_audio_description,
                isGranted = isAudioRecordPermissionGranted,
                onRequest = ::requestAudioPermission
            )
        )

    internal val recordAudioPermissionState = _recordAudioPermissionState.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("ResourceType")
    private val _postNotificationPermissionState: MutableStateFlow<RequestedPermissionModel> =
        MutableStateFlow(
            RequestedPermissionModel(
                description = R.string.Post_notification_permission_descrption,
                isGranted = isPostNotificationPermissionGranted,
                onRequest = ::requestPostNotificationPermission
            )
        )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    internal val postNotificationPermissionState = _postNotificationPermissionState.asStateFlow()

    fun regenerateButtonGradientColor() {
        _currentWidgetColor.update { it.newRecordGradient }
    }

    fun startRecord() {
        if(!isAllPermissionGranted) {
            showRequestPermissionDialog()
            return
        }

        viewModelScope.launch {
            try {
                recordManageContract.start()
            }catch (e: OtherRecordStartedException) {
               snackBarHostState?.showSnackbar(context.getString(R.string.Video_recording_has_started))
            }
        }
    }

    fun stopRecord() {
        viewModelScope.launch { recordManageContract.stop() }
    }

    fun pauseRecord() {
        viewModelScope.launch { recordManageContract.pause() }
    }

    fun resumeRecord() {
        viewModelScope.launch { recordManageContract.resume() }
    }

    private fun requestAudioPermission() {
        requestAudioRecordPermissionContract?.launch(Manifest.permission.RECORD_AUDIO)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPostNotificationPermission() {
        requestPostNotificationPermissionContract?.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun showRequestPermissionDialog() {
        _dialogState.update { it.copy(isPermissionDialogVisible = true) }
    }

    fun hideRequestPermissionDialog() {
        _dialogState.update { it.copy(isPermissionDialogVisible = false) }
    }

    fun onRequestAudioRecordPermissionResult(isGranted: Boolean) {
        _recordAudioPermissionState.update { it.copy(isGranted = isGranted) }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun onRequestPostNotificationPermissionResult(isGranted: Boolean) {
        _postNotificationPermissionState.update { it.copy(isGranted = isGranted) }
    }

    internal fun initRequestPermissionsContracts(
        requestAudioRecordPermissionContract: ActivityResultLauncher<String>,
        requestPostNotificationPermissionContract: ActivityResultLauncher<String>?,
    ) {
        this.requestAudioRecordPermissionContract = requestAudioRecordPermissionContract
        this.requestPostNotificationPermissionContract = requestPostNotificationPermissionContract
    }

    fun showToast(text: Int) {
        toastManager.showToast(text)
    }

    fun setupDelayRecord(time: Long) {
        hideDelayStartRecordConfigurationDialog()
    }

    internal var lastRecordState: RecordState = RecordState.Idle
}