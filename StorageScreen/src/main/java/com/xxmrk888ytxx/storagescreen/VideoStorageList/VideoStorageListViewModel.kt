package com.xxmrk888ytxx.storagescreen.VideoStorageList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.coreandroid.Navigator
import com.xxmrk888ytxx.storagescreen.VideoStorageList.contract.DeleteVideoFileContract
import com.xxmrk888ytxx.storagescreen.VideoStorageList.contract.OpenVideoContract
import com.xxmrk888ytxx.storagescreen.VideoStorageList.contract.ProvideVideoFilesContract
import com.xxmrk888ytxx.storagescreen.VideoStorageList.models.DialogState
import com.xxmrk888ytxx.storagescreen.VideoStorageList.models.RenameDialogState
import com.xxmrk888ytxx.storagescreen.VideoStorageList.models.VideoFileModel
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
    private val openVideoContract: OpenVideoContract
) : ViewModel() {

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
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun removeFile(id:Long) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteVideoFileContract.removeFile(id)
        }
    }

    fun changeFileName(videoId: Long,newName:String) {
        //TODO
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