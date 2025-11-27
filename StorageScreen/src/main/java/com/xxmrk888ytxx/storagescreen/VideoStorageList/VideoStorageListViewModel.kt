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
import javax.inject.Inject

class VideoStorageListViewModel @Inject constructor(
    private val deleteVideoFileContract: DeleteVideoFileContract,
    private val provideVideoFileContract: ProvideVideoFilesContract,
    private val openVideoContract: OpenVideoContract,
    private val changeVideoFileNameContract: ChangeVideoFileNameContract,
    private val toastManager: ToastManager,
    private val exportVideoContract: ExportVideoContract
) : ViewModel() {

    //ExportFile

    private var currentExportFileId = Long.MIN_VALUE
    @SuppressLint("ResourceType")
    fun onExportPathSelected(uri: Uri?) {
        if(currentExportFileId == Long.MIN_VALUE) return

        if(uri == null) {
            toastManager.showToast(R.string.Canceled)
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                _dialogState.update { it.copy(isExportLoadingDialogVisible = true) }

                exportVideoContract.exportVideo(currentExportFileId,uri)
                    .onSuccess { toastManager.showToast(R.string.Exported_successfully) }
                    .onFailure { toastManager.showToast(R.string.export_error) }

                currentExportFileId = Long.MIN_VALUE

                _dialogState.update { it.copy(isExportLoadingDialogVisible = false) }
            }
        }
    }

    fun sendExportPathRequest(videoId: Long, activityResultLauncher: ActivityResultLauncher<FileParams>) {
        currentExportFileId = videoId

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

    fun removeFile(id:Long) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteVideoFileContract.removeFile(id)
        }
    }

    fun changeFileName(videoId: Long,newName:String) {
        hideRenameDialog()
        viewModelScope.launch(Dispatchers.IO) {
            changeVideoFileNameContract.changeFileName(videoId,newName)
        }
    }
    //

    //Open video
    internal fun openVideo(videoId:Long,navigator: Navigator) {
        viewModelScope.launch(Dispatchers.IO) {
            openVideoContract.openVideoContract(videoId, navigator)
        }
    }
    //

}