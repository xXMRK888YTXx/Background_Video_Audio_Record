package com.xxmrk888ytxx.storagescreen.VideoStorageList

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.coreandroid.Navigator
import com.xxmrk888ytxx.coreandroid.ToastManager
import com.xxmrk888ytxx.coreandroid.ActivityContracts.FileParams
import com.xxmrk888ytxx.storagescreen.R
import com.xxmrk888ytxx.storagescreen.VideoStorageList.contract.ChangeVideoFileNameContract
import com.xxmrk888ytxx.storagescreen.VideoStorageList.contract.DeleteVideoFileContract
import com.xxmrk888ytxx.storagescreen.VideoStorageList.contract.ExportVideoContract
import com.xxmrk888ytxx.storagescreen.VideoStorageList.contract.OpenVideoContract
import com.xxmrk888ytxx.storagescreen.VideoStorageList.contract.ProvideVideoFilesContract
import com.xxmrk888ytxx.storagescreen.VideoStorageList.models.DialogState
import com.xxmrk888ytxx.storagescreen.VideoStorageList.models.RenameDialogState
import com.xxmrk888ytxx.storagescreen.VideoStorageList.models.VideoFileModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

import com.xxmrk888ytxx.coreandroid.models.DualCameraPrimary
import com.xxmrk888ytxx.coreandroid.models.DualCameraSaveOption
import com.xxmrk888ytxx.coreandroid.models.PipPosition
import com.xxmrk888ytxx.storagescreen.VideoStorageList.contract.ManageDualCameraLayoutContract

class VideoStorageListViewModel @Inject constructor(
    private val deleteVideoFileContract: DeleteVideoFileContract,
    private val provideVideoFileContract: ProvideVideoFilesContract,
    private val openVideoContract: OpenVideoContract,
    private val changeVideoFileNameContract: ChangeVideoFileNameContract,
    private val toastManager: ToastManager,
    private val exportVideoContract: ExportVideoContract,
    private val manageDualCameraLayoutContract: ManageDualCameraLayoutContract
) : ViewModel() {

    val dualCameraPrimary = manageDualCameraLayoutContract.primaryCamera
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DualCameraPrimary.BACK)

    val pipPosition = manageDualCameraLayoutContract.pipPosition
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PipPosition.TOP_RIGHT)

    fun setDualCameraPrimary(primary: DualCameraPrimary) {
        viewModelScope.launch(Dispatchers.IO) {
            manageDualCameraLayoutContract.setPrimaryCamera(primary)
        }
    }

    fun setPipPosition(position: PipPosition) {
        viewModelScope.launch(Dispatchers.IO) {
            manageDualCameraLayoutContract.setPipPosition(position)
        }
    }

    //ExportFile

    private var currentExportFileId = Long.MIN_VALUE
    private var currentDualOption: DualCameraSaveOption? = null
    private var isExportingSecondFileForDual = false
    private var pendingLauncher: ActivityResultLauncher<FileParams>? = null

    fun onExportClick(video: VideoFileModel, launcher: ActivityResultLauncher<FileParams>) {
        if (video.isDualRecord) {
            currentExportFileId = video.id
            isExportingSecondFileForDual = false
            pendingLauncher = launcher
            _dialogState.update { it.copy(dualExportDialogVideoId = video.id) }
        } else {
            currentDualOption = null
            isExportingSecondFileForDual = false
            sendExportPathRequest(video.id, launcher)
        }
    }

    fun onDualOptionSelected(option: DualCameraSaveOption) {
        val videoId = _dialogState.value.dualExportDialogVideoId ?: return
        _dialogState.update { it.copy(dualExportDialogVideoId = null) }
        currentDualOption = option
        isExportingSecondFileForDual = false

        val fileName = when (option) {
            DualCameraSaveOption.BACK_ONLY -> "video_back.mp4"
            DualCameraSaveOption.FRONT_ONLY -> "video_front.mp4"
            DualCameraSaveOption.BOTH_TOGETHER -> "video_combined.mp4"
            DualCameraSaveOption.BOTH_SEPARATELY -> "video_back.mp4"
        }

        pendingLauncher?.launch(
            FileParams(
                fileType = "video/mp4",
                startFileName = fileName
            )
        )
    }

    fun hideDualExportDialog() {
        _dialogState.update { it.copy(dualExportDialogVideoId = null) }
        pendingLauncher = null
        isExportingSecondFileForDual = false
    }

    @SuppressLint("ResourceType")
    fun onExportPathSelected(uri: Uri?) {
        if(currentExportFileId == Long.MIN_VALUE) return

        if(uri == null) {
            toastManager.showToast(R.string.Canceled)
            currentExportFileId = Long.MIN_VALUE
            currentDualOption = null
            isExportingSecondFileForDual = false
            pendingLauncher = null
            return
        }

        val videoId = currentExportFileId
        val dualOption = currentDualOption

        viewModelScope.launch(Dispatchers.IO) {
            _dialogState.update { it.copy(isExportLoadingDialogVisible = true) }

            try {
                if (dualOption == DualCameraSaveOption.BOTH_SEPARATELY) {
                    if (!isExportingSecondFileForDual) {
                        // First step: save back camera video
                        val result = exportVideoContract.exportDualVideo(videoId, uri, DualCameraSaveOption.BACK_ONLY)
                        if (result.isSuccess) {
                            isExportingSecondFileForDual = true
                            withContext(Dispatchers.Main) {
                                toastManager.showToast(R.string.save_dual_second_prompt)
                                kotlinx.coroutines.delay(350)
                                pendingLauncher?.launch(
                                    FileParams(
                                        fileType = "video/mp4",
                                        startFileName = "video_front.mp4"
                                    )
                                )
                            }
                        } else {
                            toastManager.showToast(R.string.export_error)
                            currentExportFileId = Long.MIN_VALUE
                            currentDualOption = null
                            isExportingSecondFileForDual = false
                            pendingLauncher = null
                        }
                    } else {
                        // Second step: save front camera video
                        val result = exportVideoContract.exportSecondaryVideo(videoId, uri)
                        if (result.isSuccess) {
                            toastManager.showToast(R.string.both_videos_exported_successfully)
                        } else {
                            toastManager.showToast(R.string.export_error)
                        }
                        currentExportFileId = Long.MIN_VALUE
                        currentDualOption = null
                        isExportingSecondFileForDual = false
                        pendingLauncher = null
                    }
                } else {
                    val result = if (dualOption != null) {
                        exportVideoContract.exportDualVideo(videoId, uri, dualOption)
                    } else {
                        exportVideoContract.exportVideo(videoId, uri)
                    }

                    result
                        .onSuccess { toastManager.showToast(R.string.Exported_successfully) }
                        .onFailure { toastManager.showToast(R.string.export_error) }

                    currentExportFileId = Long.MIN_VALUE
                    currentDualOption = null
                    isExportingSecondFileForDual = false
                    pendingLauncher = null
                }
            } finally {
                _dialogState.update { it.copy(isExportLoadingDialogVisible = false) }
            }
        }
    }

    fun sendExportPathRequest(videoId: Long, activityResultLauncher: ActivityResultLauncher<FileParams>) {
        currentExportFileId = videoId
        currentDualOption = null

        activityResultLauncher.launch(
            FileParams(
                fileType = "video/mp4",
                startFileName = "video.mp4"
            )
        )
    }

    //

    //Dialogs
    private val _dialogState = MutableStateFlow(DialogState())

    internal val dialogState = _dialogState.asStateFlow()

    internal fun showRenameDialog(file:VideoFileModel) {

        _dialogState.update {
            it.copy(renameDialogState = RenameDialogState.Showed(
                file.id,
                file.name ?: ""
            ))
        }
    }

    internal fun hideRenameDialog() {
        _dialogState.update {
            it.copy(renameDialogState = RenameDialogState.Hidden)
        }
    }
    //

    //files
    internal val videoFiles = provideVideoFileContract.videoFiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), persistentListOf())

    fun requestDelete(id: Long) {
        _dialogState.update { it.copy(pendingDeleteVideoId = id) }
    }

    fun cancelDelete() {
        _dialogState.update { it.copy(pendingDeleteVideoId = null) }
    }

    fun confirmDelete() {
        val id = _dialogState.value.pendingDeleteVideoId ?: return
        _dialogState.update { it.copy(pendingDeleteVideoId = null) }
        removeFile(id)
    }

    fun removeFile(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteVideoFileContract.removeFile(id)
        }
    }

    fun changeFileName(videoId: Long, newName: String) {
        hideRenameDialog()
        viewModelScope.launch(Dispatchers.IO) {
            changeVideoFileNameContract.changeFileName(videoId, newName)
        }
    }
    //

    //Open video
    internal fun onPlayVideoClick(video: VideoFileModel, navigator: Navigator) {
        if (video.isDualRecord) {
            _dialogState.update { it.copy(dualPlayDialogVideoId = video.id) }
        } else {
            openVideo(video.id, navigator)
        }
    }

    internal fun hideDualPlayDialog() {
        _dialogState.update { it.copy(dualPlayDialogVideoId = null) }
    }

    internal fun onDualPlayOptionSelected(option: DualCameraSaveOption, navigator: Navigator) {
        val videoId = _dialogState.value.dualPlayDialogVideoId ?: return
        _dialogState.update { it.copy(dualPlayDialogVideoId = null) }
        viewModelScope.launch(Dispatchers.IO) {
            if (option == DualCameraSaveOption.BOTH_TOGETHER) {
                _dialogState.update { it.copy(isExportLoadingDialogVisible = true) }
            }
            try {
                openVideoContract.openDualVideoContract(videoId, option, navigator)
            } finally {
                _dialogState.update { it.copy(isExportLoadingDialogVisible = false) }
            }
        }
    }

    internal fun openVideo(videoId: Long, navigator: Navigator) {
        viewModelScope.launch(Dispatchers.IO) {
            openVideoContract.openVideoContract(videoId, navigator)
        }
    }
    //

}